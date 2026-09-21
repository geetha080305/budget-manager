package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WeeklyReportResponse(
        LocalDate weekStart, // Monday
        LocalDate weekEnd,   // Sunday
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        List<DailyTotal> dailyTotals // always 7 entries, zero-filled for days with no activity
) {}
