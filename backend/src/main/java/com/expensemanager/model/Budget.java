package com.expensemanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A budget for a given (user, year, month). If category is null, this
 * is the overall monthly budget (Section 4.4 "Set monthly budgets");
 * if category is set, it's a category-wise budget.
 */
@Entity
@Table(name = "budgets", uniqueConstraints = {
        // One budget row per user/category/month - prevents duplicate/ambiguous budgets.
        @UniqueConstraint(name = "uk_budget_user_category_period",
                columnNames = {"user_id", "category", "budget_year", "budget_month"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Null = overall monthly budget, not tied to one category.
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ExpenseCategory category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "budget_year", nullable = false)
    private Integer budgetYear;

    // 1-12
    @Column(name = "budget_month", nullable = false)
    private Integer budgetMonth;
}
