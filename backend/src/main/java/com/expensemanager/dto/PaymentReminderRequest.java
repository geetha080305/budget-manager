package com.expensemanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentReminderRequest(
        @NotBlank(message = "Bill name is required") String billName,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,
        @NotNull(message = "Due date is required") LocalDate dueDate,
        boolean recurring
) {}
