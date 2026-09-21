package com.expensemanager.dto;

import com.expensemanager.model.ExpenseCategory;
import com.expensemanager.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        ExpenseCategory category,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        LocalDate date,
        String description,
        BudgetAlert budgetAlert
) {
    /**
     * Null when no budget is configured for this expense's category+month
     * AND no overall monthly budget is configured either. Otherwise carries
     * the current spend-vs-budget status (category budget takes priority
     * over the overall budget when both exist) - this is the server-side
     * budget-alert hook the frontend reads to show a warning.
     */
    public record BudgetAlert(
            String scope,       // "CATEGORY" or "OVERALL"
            BigDecimal budget,
            BigDecimal spent,
            BigDecimal remaining,
            boolean exceeded
    ) {}
}
