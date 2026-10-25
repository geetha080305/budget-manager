package com.expensemanager.service;

import com.expensemanager.dto.*;
import com.expensemanager.model.Expense;
import com.expensemanager.model.ExpenseCategory;
import com.expensemanager.model.Income;
import com.expensemanager.model.User;
import com.expensemanager.repository.ExpenseRepository;
import com.expensemanager.repository.IncomeRepository;
import com.expensemanager.repository.SavingsGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int RECENT_TRANSACTIONS_LIMIT = 5;

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final BudgetService budgetService;
    private final HouseholdService householdService;

    public DashboardResponse getDashboard(User user, Integer year, Integer month) {
        int y = year != null ? year : LocalDate.now().getYear();
        int m = month != null ? month : LocalDate.now().getMonthValue();
        YearMonth ym = YearMonth.of(y, m);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Long> userIds = householdService.getEffectiveUserIds(user);

        BigDecimal totalIncome = incomeRepository.sumByUserIdInAndDateBetween(userIds, start, end);
        BigDecimal totalExpenses = expenseRepository.sumByUserIdInAndDateBetween(userIds, start, end);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        BigDecimal savingsGoalsSaved = savingsGoalRepository.findByUserIdInOrderByIdDesc(userIds).stream()
                .map(g -> g.getSavedAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TransactionSummary> recentTransactions = recentTransactions(userIds);

        List<BudgetResponse> budgetStatus = budgetService.list(user, y, m);

        List<ExpenseRepository.CategoryTotal> grouped =
                expenseRepository.sumGroupedByCategory(userIds, start, end);
        List<CategoryBreakdown> expenseByCategory = grouped.stream()
                .map(ct -> new CategoryBreakdown(ct.getCategory(), ct.getTotal()))
                .toList();
        CategoryBreakdown highest = expenseByCategory.isEmpty() ? null : expenseByCategory.get(0);

        return new DashboardResponse(
                y, m, totalIncome, totalExpenses, balance,
                balance,               // totalSavings mirrors balance, per the doc's own example
                savingsGoalsSaved,
                recentTransactions, budgetStatus, highest, expenseByCategory);
    }

    private List<TransactionSummary> recentTransactions(List<Long> userIds) {
        Stream<TransactionSummary> incomeStream = incomeRepository.findTop5ByUserIdInOrderByDateDesc(userIds)
                .stream().map(this::toSummary);
        Stream<TransactionSummary> expenseStream = expenseRepository.findTop5ByUserIdInOrderByDateDesc(userIds)
                .stream().map(this::toSummary);

        return Stream.concat(incomeStream, expenseStream)
                .sorted(Comparator.comparing(TransactionSummary::date).reversed())
                .limit(RECENT_TRANSACTIONS_LIMIT)
                .toList();
    }

    private TransactionSummary toSummary(Income income) {
        User u = income.getUser();
        String username = u != null ? u.getUsername() : null;
        String fullName = u != null && u.getFullName() != null && !u.getFullName().isBlank() ? u.getFullName() : username;
        return new TransactionSummary("INCOME", income.getId(), income.getSource(),
                income.getAmount(), income.getDate(), username, fullName);
    }

    private TransactionSummary toSummary(Expense expense) {
        User u = expense.getUser();
        String username = u != null ? u.getUsername() : null;
        String fullName = u != null && u.getFullName() != null && !u.getFullName().isBlank() ? u.getFullName() : username;
        return new TransactionSummary("EXPENSE", expense.getId(), expense.getCategory().name(),
                expense.getAmount(), expense.getDate(), username, fullName);
    }
}
