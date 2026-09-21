package com.expensemanager.repository;

import com.expensemanager.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserIdOrderByDateDesc(Long userId);

    List<Income> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate start, LocalDate end);

    List<Income> findTop5ByUserIdOrderByDateDesc(Long userId);

    @org.springframework.data.jpa.repository.Query(
            "SELECT COALESCE(SUM(i.amount), 0) FROM Income i " +
            "WHERE i.user.id = :userId AND i.date BETWEEN :start AND :end")
    BigDecimal sumByUserIdAndDateBetween(
            @org.springframework.data.repository.query.Param("userId") Long userId,
            @org.springframework.data.repository.query.Param("start") LocalDate start,
            @org.springframework.data.repository.query.Param("end") LocalDate end);
}
