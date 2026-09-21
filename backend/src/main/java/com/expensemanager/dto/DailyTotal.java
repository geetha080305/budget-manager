package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyTotal(
        LocalDate date,
        BigDecimal income,
        BigDecimal expense
) {}
