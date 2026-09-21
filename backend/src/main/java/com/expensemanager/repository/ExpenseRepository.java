package com.expensemanager.repository;

import com.expensemanager.model.Expense;
import com.expensemanager.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate start, LocalDate end);

    List<Expense> findByUserIdAndCategoryOrderByDateDesc(
            Long userId, ExpenseCategory category);

    List<Expense> findTop5ByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    BigDecimal sumByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.category = :category " +
           "AND e.date BETWEEN :start AND :end")
    BigDecimal sumByUserIdAndCategoryAndDateBetween(
            @Param("userId") Long userId,
            @Param("category") ExpenseCategory category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // Powers "highest spending category" on the dashboard.
    @Query("SELECT e.category AS category, COALESCE(SUM(e.amount), 0) AS total " +
           "FROM Expense e WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end " +
           "GROUP BY e.category ORDER BY total DESC")
    List<CategoryTotal> sumGroupedByCategory(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    interface CategoryTotal {
        ExpenseCategory getCategory();
        BigDecimal getTotal();
    }
}
