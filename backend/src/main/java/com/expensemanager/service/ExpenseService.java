package com.expensemanager.service;

import com.expensemanager.dto.ExpenseRequest;
import com.expensemanager.dto.ExpenseResponse;
import com.expensemanager.model.Expense;
import com.expensemanager.model.ExpenseCategory;
import com.expensemanager.model.User;
import com.expensemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetAlertService budgetAlertService;
    private final HouseholdService householdService;

    @Transactional
    public ExpenseResponse create(User user, ExpenseRequest request) {
        Expense expense = Expense.builder()
                .user(user)
                .category(request.category())
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .date(request.date())
                .description(request.description())
                .build();
        expense = expenseRepository.save(expense);
        return toResponse(expense);
    }

    public List<ExpenseResponse> list(User user, LocalDate start, LocalDate end, ExpenseCategory category) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        List<Expense> results;
        if (category != null) {
            results = expenseRepository.findByUserIdInAndCategoryOrderByDateDesc(userIds, category);
        } else if (start != null && end != null) {
            results = expenseRepository.findByUserIdInAndDateBetweenOrderByDateDesc(userIds, start, end);
        } else {
            results = expenseRepository.findByUserIdInOrderByDateDesc(userIds);
        }
        return results.stream().map(this::toResponseWithoutAlert).toList();
    }

    public ExpenseResponse get(User user, Long id) {
        return toResponse(findOwned(user, id));
    }

    @Transactional
    public ExpenseResponse update(User user, Long id, ExpenseRequest request) {
        Expense expense = findOwned(user, id);
        expense.setCategory(request.category());
        expense.setAmount(request.amount());
        expense.setPaymentMethod(request.paymentMethod());
        expense.setDate(request.date());
        expense.setDescription(request.description());
        return toResponse(expense);
    }

    @Transactional
    public void delete(User user, Long id) {
        Expense expense = findOwned(user, id);
        expenseRepository.delete(expense);
    }

    private Expense findOwned(User user, Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        if (!userIds.contains(expense.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found");
        }
        return expense;
    }

    private ExpenseResponse toResponse(Expense expense) {
        List<Long> userIds = householdService.getEffectiveUserIds(expense.getUser());
        var alert = budgetAlertService.computeAlert(
                userIds, expense.getCategory(), expense.getDate());
        return build(expense, alert);
    }

    private ExpenseResponse toResponseWithoutAlert(Expense expense) {
        return build(expense, null);
    }

    private ExpenseResponse build(Expense expense, ExpenseResponse.BudgetAlert alert) {
        User u = expense.getUser();
        String username = u != null ? u.getUsername() : null;
        String fullName = u != null && u.getFullName() != null && !u.getFullName().isBlank() ? u.getFullName() : username;
        return new ExpenseResponse(
                expense.getId(), expense.getCategory(), expense.getAmount(),
                expense.getPaymentMethod(), expense.getDate(), expense.getDescription(), alert,
                username, fullName);
    }
}
