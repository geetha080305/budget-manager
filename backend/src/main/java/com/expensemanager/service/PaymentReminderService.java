package com.expensemanager.service;

import com.expensemanager.dto.PaymentReminderRequest;
import com.expensemanager.dto.PaymentReminderResponse;
import com.expensemanager.model.PaymentReminder;
import com.expensemanager.model.User;
import com.expensemanager.repository.PaymentReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.expensemanager.model.Expense;
import com.expensemanager.model.ExpenseCategory;
import com.expensemanager.model.PaymentMethod;
import com.expensemanager.repository.ExpenseRepository;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentReminderService {

    // A bill due within this many days (but not yet overdue) shows as DUE rather than UPCOMING.
    private static final int DUE_SOON_WINDOW_DAYS = 3;

    private final PaymentReminderRepository reminderRepository;
    private final ExpenseRepository expenseRepository;
    private final HouseholdService householdService;

    @Transactional
    public PaymentReminderResponse create(User user, PaymentReminderRequest request) {
        PaymentReminder reminder = PaymentReminder.builder()
                .user(user)
                .billName(request.billName())
                .amount(request.amount())
                .dueDate(request.dueDate())
                .recurring(request.recurring())
                .build(); // status defaults to UPCOMING via @Builder.Default
        return toResponse(reminderRepository.save(reminder));
    }

    public List<PaymentReminderResponse> list(User user) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        return reminderRepository.findByUserIdInOrderByDueDateAsc(userIds)
                .stream().map(this::toResponse).toList();
    }

    public PaymentReminderResponse get(User user, Long id) {
        return toResponse(findOwned(user, id));
    }

    @Transactional
    public PaymentReminderResponse update(User user, Long id, PaymentReminderRequest request) {
        PaymentReminder reminder = findOwned(user, id);
        reminder.setBillName(request.billName());
        reminder.setAmount(request.amount());
        reminder.setDueDate(request.dueDate());
        reminder.setRecurring(request.recurring());
        return toResponse(reminder);
    }

    /**
     * Marks the bill paid. If it's recurring, immediately creates next
     * month's reminder (same name/amount, due date +1 month, fresh UPCOMING
     * status) so recurring bills (Section 4.8: electricity, rent,
     * subscriptions) don't need re-entering by hand every cycle.
     */
    @Transactional
    public PaymentReminderResponse markPaid(User user, Long id) {
        PaymentReminder reminder = findOwned(user, id);
        reminder.setStatus(PaymentReminder.Status.PAID);

        // Auto-record in Expenses so Total Expenses, Balance, and Recent Transactions update immediately
        ExpenseCategory category = ExpenseCategory.BILLS;
        String nameLower = reminder.getBillName().toLowerCase();
        if (nameLower.contains("rent")) {
            category = ExpenseCategory.RENT;
        } else if (nameLower.contains("fee") || nameLower.contains("tuition") || nameLower.contains("course")) {
            category = ExpenseCategory.EDUCATION;
        }

        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .amount(reminder.getAmount())
                .paymentMethod(PaymentMethod.UPI)
                .date(LocalDate.now())
                .description("Bill Paid: " + reminder.getBillName())
                .build();
        expenseRepository.save(expense);

        if (reminder.isRecurring()) {
            PaymentReminder next = PaymentReminder.builder()
                    .user(user)
                    .billName(reminder.getBillName())
                    .amount(reminder.getAmount())
                    .dueDate(reminder.getDueDate().plusMonths(1))
                    .recurring(true)
                    .status(PaymentReminder.Status.UPCOMING)
                    .build();
            reminderRepository.save(next);
        }

        return toResponse(reminder);
    }

    @Transactional
    public void delete(User user, Long id) {
        reminderRepository.delete(findOwned(user, id));
    }

    private PaymentReminder findOwned(User user, Long id) {
        PaymentReminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment reminder not found"));
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        if (!userIds.contains(reminder.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment reminder not found");
        }
        return reminder;
    }

    /**
     * Computes the *effective* status for display without mutating the
     * stored row (a plain read shouldn't have side effects). PAID is
     * left as-is; everything else is derived from today's date vs. due date.
     */
    private PaymentReminder.Status effectiveStatus(PaymentReminder reminder) {
        if (reminder.getStatus() == PaymentReminder.Status.PAID) {
            return PaymentReminder.Status.PAID;
        }
        LocalDate today = LocalDate.now();
        if (reminder.getDueDate().isBefore(today)) {
            return PaymentReminder.Status.OVERDUE;
        }
        if (!reminder.getDueDate().isAfter(today.plusDays(DUE_SOON_WINDOW_DAYS))) {
            return PaymentReminder.Status.DUE;
        }
        return PaymentReminder.Status.UPCOMING;
    }

    private PaymentReminderResponse toResponse(PaymentReminder reminder) {
        return new PaymentReminderResponse(
                reminder.getId(), reminder.getBillName(), reminder.getAmount(),
                reminder.getDueDate(), effectiveStatus(reminder), reminder.isRecurring());
    }
}
