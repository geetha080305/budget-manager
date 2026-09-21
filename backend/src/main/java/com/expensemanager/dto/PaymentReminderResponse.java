package com.expensemanager.dto;

import com.expensemanager.model.PaymentReminder;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentReminderResponse(
        Long id,
        String billName,
        BigDecimal amount,
        LocalDate dueDate,
        PaymentReminder.Status status, // computed live (UPCOMING/DUE/OVERDUE) unless already PAID
        boolean recurring
) {}
