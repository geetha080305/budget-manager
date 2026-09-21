package com.expensemanager.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyReportResponse(
        int year,
        int month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        List<DailyTotal> dailyTotals,           // one entry per day of the month, zero-filled
        List<CategoryBreakdown> categoryBreakdown
) {}
