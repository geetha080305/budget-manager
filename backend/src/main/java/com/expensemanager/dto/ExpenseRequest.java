package com.expensemanager.dto;

import com.expensemanager.model.ExpenseCategory;
import com.expensemanager.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotNull(message = "Category is required") ExpenseCategory category,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,
        @NotNull(message = "Payment method is required") PaymentMethod paymentMethod,
        @NotNull(message = "Date is required") LocalDate date,
        String description
) {}
