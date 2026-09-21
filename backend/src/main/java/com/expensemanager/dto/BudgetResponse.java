package com.expensemanager.dto;

import com.expensemanager.model.ExpenseCategory;

import java.math.BigDecimal;

public record BudgetResponse(
        Long id,
        ExpenseCategory category, // null = overall monthly budget
        BigDecimal amount,
        Integer budgetYear,
        Integer budgetMonth,
        BigDecimal spent,
        BigDecimal remaining,
        boolean exceeded
) {}
