package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CategoryAnalysisResponse(
        LocalDate start,
        LocalDate end,
        BigDecimal totalExpense,
        List<CategoryShare> categories // sorted highest spend first
) {}
