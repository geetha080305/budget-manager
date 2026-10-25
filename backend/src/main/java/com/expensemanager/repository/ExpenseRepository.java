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

    List<Expense> findByUserIdInOrderByDateDesc(List<Long> userIds);

    List<Expense> findByUserIdInAndDateBetweenOrderByDateDesc(
            List<Long> userIds, LocalDate start, LocalDate end);

    List<Expense> findByUserIdInAndCategoryOrderByDateDesc(
            List<Long> userIds, ExpenseCategory category);

    List<Expense> findTop5ByUserIdInOrderByDateDesc(List<Long> userIds);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id IN :userIds AND e.date BETWEEN :start AND :end")
    BigDecimal sumByUserIdInAndDateBetween(
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id IN :userIds AND e.category = :category " +
           "AND e.date BETWEEN :start AND :end")
    BigDecimal sumByUserIdInAndCategoryAndDateBetween(
            @Param("userIds") List<Long> userIds,
            @Param("category") ExpenseCategory category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // Powers "highest spending category" on the dashboard.
    @Query("SELECT e.category AS category, COALESCE(SUM(e.amount), 0) AS total " +
           "FROM Expense e WHERE e.user.id IN :userIds AND e.date BETWEEN :start AND :end " +
           "GROUP BY e.category ORDER BY total DESC")
    List<CategoryTotal> sumGroupedByCategory(
            @Param("userIds") List<Long> userIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    interface CategoryTotal {
        ExpenseCategory getCategory();
        BigDecimal getTotal();
    }
}
