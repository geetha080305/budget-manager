package com.expensemanager.dto;

import com.expensemanager.model.ExpenseCategory;

import java.math.BigDecimal;

public record CategoryBreakdown(
        ExpenseCategory category,
        BigDecimal total
) {}
