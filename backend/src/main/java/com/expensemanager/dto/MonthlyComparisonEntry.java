package com.expensemanager.dto;

import java.math.BigDecimal;

public record MonthlyComparisonEntry(
        int year,
        int month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {}
