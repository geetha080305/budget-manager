package com.expensemanager.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        int year,
        int month,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance,
        // Mirrors the requirements doc's dashboard example, where "Savings"
        // equals the unspent balance for the period. Distinct from savings
        // GOALS progress, which is a separate concept - see savingsGoalsSaved.
        BigDecimal totalSavings,
        BigDecimal savingsGoalsSaved, // sum of savedAmount across all active savings goals
        List<TransactionSummary> recentTransactions,
        List<BudgetResponse> budgetStatus,
        CategoryBreakdown highestSpendingCategory, // null if no expenses this period
        List<CategoryBreakdown> expenseByCategory
) {}
