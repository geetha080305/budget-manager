package com.expensemanager.dto;

import com.expensemanager.model.ExpenseCategory;

import java.math.BigDecimal;

public record CategoryShare(
        ExpenseCategory category,
        BigDecimal total,
        double percentageOfTotal // 0-100, rounded to 1 decimal
) {}
