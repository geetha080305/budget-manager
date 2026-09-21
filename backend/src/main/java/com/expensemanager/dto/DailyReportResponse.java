package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DailyReportResponse(
        LocalDate date,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        List<TransactionSummary> transactions
) {}
