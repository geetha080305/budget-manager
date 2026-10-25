package com.expensemanager.service;

import com.expensemanager.dto.*;
import com.expensemanager.model.Expense;
import com.expensemanager.model.Income;
import com.expensemanager.model.User;
import com.expensemanager.repository.ExpenseRepository;
import com.expensemanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final HouseholdService householdService;

    // ----- 4.7 Daily report -----

    public DailyReportResponse getDaily(User user, LocalDate date) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        List<Income> incomes = incomeRepository.findByUserIdInAndDateBetweenOrderByDateDesc(userIds, date, date);
        List<Expense> expenses = expenseRepository.findByUserIdInAndDateBetweenOrderByDateDesc(userIds, date, date);

        BigDecimal totalIncome = sum(incomes, Income::getAmount);
        BigDecimal totalExpense = sum(expenses, Expense::getAmount);

        List<TransactionSummary> transactions = Stream.concat(
                        incomes.stream().map(this::toSummary),
                        expenses.stream().map(this::toSummary))
                .sorted(Comparator.comparing(TransactionSummary::date).reversed())
                .toList();

        return new DailyReportResponse(date, totalIncome, totalExpense, transactions);
    }

    // ----- 4.7 Weekly report (Monday-Sunday containing the given date) -----

    public WeeklyReportResponse getWeekly(User user, LocalDate anyDateInWeek) {
        LocalDate weekStart = anyDateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = anyDateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return buildRangeReport(user, weekStart, weekEnd,
                (totalIncome, totalExpense, dailyTotals) ->
                        new WeeklyReportResponse(weekStart, weekEnd, totalIncome, totalExpense, dailyTotals));
    }

    // ----- 4.7 Monthly report -----

    public MonthlyReportResponse getMonthly(User user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Long> userIds = householdService.getEffectiveUserIds(user);

        BigDecimal totalIncome = incomeRepository.sumByUserIdInAndDateBetween(userIds, start, end);
        BigDecimal totalExpense = expenseRepository.sumByUserIdInAndDateBetween(userIds, start, end);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        List<DailyTotal> dailyTotals = buildDailyTotals(user, start, end);

        List<ExpenseRepository.CategoryTotal> grouped =
                expenseRepository.sumGroupedByCategory(userIds, start, end);
        List<CategoryBreakdown> categoryBreakdown = grouped.stream()
                .map(ct -> new CategoryBreakdown(ct.getCategory(), ct.getTotal()))
                .toList();

        return new MonthlyReportResponse(year, month, totalIncome, totalExpense, balance, dailyTotals, categoryBreakdown);
    }

    // ----- 4.7 Category-wise expense analysis -----

    public CategoryAnalysisResponse getCategoryAnalysis(User user, LocalDate start, LocalDate end) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        List<ExpenseRepository.CategoryTotal> grouped =
                expenseRepository.sumGroupedByCategory(userIds, start, end);

        BigDecimal total = grouped.stream()
                .map(ExpenseRepository.CategoryTotal::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoryShare> shares = grouped.stream()
                .map(ct -> new CategoryShare(
                    ct.getCategory(),
                    ct.getTotal(),
                    percentageOf(ct.getTotal(), total)))
                .sorted(Comparator.comparing(CategoryShare::total).reversed())
                .toList();

        return new CategoryAnalysisResponse(start, end, total, shares);
    }

    // ----- 4.7 Income vs expense analysis -----

    public IncomeVsExpenseResponse getIncomeVsExpense(User user, LocalDate start, LocalDate end) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        BigDecimal totalIncome = incomeRepository.sumByUserIdInAndDateBetween(userIds, start, end);
        BigDecimal totalExpense = expenseRepository.sumByUserIdInAndDateBetween(userIds, start, end);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        Double savingsRate = totalIncome.signum() == 0
                ? null
                : balance.divide(totalIncome, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(1, RoundingMode.HALF_UP)
                        .doubleValue();

        return new IncomeVsExpenseResponse(start, end, totalIncome, totalExpense, balance, savingsRate);
    }

    // ----- 4.7/5.3 Monthly comparison & spending trend -----

    public List<MonthlyComparisonEntry> getMonthlyComparison(User user, int months) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        YearMonth current = YearMonth.now();
        List<MonthlyComparisonEntry> result = new ArrayList<>();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            BigDecimal totalIncome = incomeRepository.sumByUserIdInAndDateBetween(userIds, start, end);
            BigDecimal totalExpense = expenseRepository.sumByUserIdInAndDateBetween(userIds, start, end);
            result.add(new MonthlyComparisonEntry(
                    ym.getYear(), ym.getMonthValue(), totalIncome, totalExpense,
                    totalIncome.subtract(totalExpense)));
        }
        return result;
    }

    // ----- shared helpers -----

    private interface RangeReportBuilder<T> {
        T build(BigDecimal totalIncome, BigDecimal totalExpense, List<DailyTotal> dailyTotals);
    }

    private <T> T buildRangeReport(User user, LocalDate start, LocalDate end, RangeReportBuilder<T> builder) {
        List<DailyTotal> dailyTotals = buildDailyTotals(user, start, end);
        BigDecimal totalIncome = dailyTotals.stream().map(DailyTotal::income).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = dailyTotals.stream().map(DailyTotal::expense).reduce(BigDecimal.ZERO, BigDecimal::add);
        return builder.build(totalIncome, totalExpense, dailyTotals);
    }

    // Zero-fills every day in [start, end] so charts don't have gaps on days with no activity.
    private List<DailyTotal> buildDailyTotals(User user, LocalDate start, LocalDate end) {
        List<Long> userIds = householdService.getEffectiveUserIds(user);
        Map<LocalDate, BigDecimal> incomeByDate = incomeRepository
                .findByUserIdInAndDateBetweenOrderByDateDesc(userIds, start, end).stream()
                .collect(Collectors.groupingBy(Income::getDate,
                        Collectors.reducing(BigDecimal.ZERO, Income::getAmount, BigDecimal::add)));

        Map<LocalDate, BigDecimal> expenseByDate = expenseRepository
                .findByUserIdInAndDateBetweenOrderByDateDesc(userIds, start, end).stream()
                .collect(Collectors.groupingBy(Expense::getDate,
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        List<DailyTotal> totals = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            totals.add(new DailyTotal(
                    d,
                    incomeByDate.getOrDefault(d, BigDecimal.ZERO),
                    expenseByDate.getOrDefault(d, BigDecimal.ZERO)));
        }
        return totals;
    }

    private double percentageOf(BigDecimal part, BigDecimal total) {
        if (total.signum() == 0) return 0.0;
        return part.divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private <T> BigDecimal sum(List<T> items, Function<T, BigDecimal> extractor) {
        return items.stream().map(extractor).reduce(BigDecimal.ZERO, BigDecimal::add);
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
