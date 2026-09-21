package com.expensemanager.repository;

import com.expensemanager.model.Budget;
import com.expensemanager.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndBudgetYearAndBudgetMonth(
            Long userId, Integer year, Integer month);

    Optional<Budget> findByUserIdAndCategoryAndBudgetYearAndBudgetMonth(
            Long userId, ExpenseCategory category, Integer year, Integer month);

    // Overall monthly budget has category = null.
    Optional<Budget> findByUserIdAndCategoryIsNullAndBudgetYearAndBudgetMonth(
            Long userId, Integer year, Integer month);
}
