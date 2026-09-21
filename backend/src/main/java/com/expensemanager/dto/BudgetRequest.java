package com.expensemanager.dto;

import com.expensemanager.model.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BudgetRequest(
        // Null = overall monthly budget (not tied to a category)
        ExpenseCategory category,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,
        @NotNull(message = "Year is required") Integer budgetYear,
        @NotNull(message = "Month is required") @Min(1) @Max(12) Integer budgetMonth
) {}
