package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeVsExpenseResponse(
        LocalDate start,
        LocalDate end,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        Double savingsRatePercent // null when totalIncome is 0 (avoids divide-by-zero)
) {}
