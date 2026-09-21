package com.expensemanager.service;

import com.expensemanager.dto.ExpenseResponse;
import com.expensemanager.model.Budget;
import com.expensemanager.model.ExpenseCategory;
import com.expensemanager.repository.BudgetRepository;
import com.expensemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class BudgetAlertService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Category budget takes priority over the overall monthly budget when
     * both are configured, since it's the more specific constraint.
     * Returns null when no relevant budget exists at all.
     */
    public ExpenseResponse.BudgetAlert computeAlert(Long userId, ExpenseCategory category, LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return budgetRepository.findByUserIdAndCategoryAndBudgetYearAndBudgetMonth(userId, category, year, month)
                .map(budget -> {
                    BigDecimal spent = expenseRepository.sumByUserIdAndCategoryAndDateBetween(
                            userId, category, start, end);
                    return toAlert("CATEGORY", budget, spent);
                })
                .orElseGet(() -> budgetRepository
                        .findByUserIdAndCategoryIsNullAndBudgetYearAndBudgetMonth(userId, year, month)
                        .map(budget -> {
                            BigDecimal spent = expenseRepository.sumByUserIdAndDateBetween(userId, start, end);
                            return toAlert("OVERALL", budget, spent);
                        })
                        .orElse(null));
    }

    private ExpenseResponse.BudgetAlert toAlert(String scope, Budget budget, BigDecimal spent) {
        BigDecimal remaining = budget.getAmount().subtract(spent);
        boolean exceeded = remaining.signum() < 0;
        return new ExpenseResponse.BudgetAlert(
                scope,
                budget.getAmount(),
                spent,
                exceeded ? BigDecimal.ZERO : remaining,
                exceeded
        );
    }
}
