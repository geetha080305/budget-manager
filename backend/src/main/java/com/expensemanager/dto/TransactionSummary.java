package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionSummary(
        String type,   // "INCOME" or "EXPENSE"
        Long id,
        String label,  // income source, or expense category name
        BigDecimal amount,
        LocalDate date
) {}
