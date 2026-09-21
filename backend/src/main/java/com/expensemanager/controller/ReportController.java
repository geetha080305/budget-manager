package com.expensemanager.controller;

import com.expensemanager.dto.*;
import com.expensemanager.model.User;
import com.expensemanager.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public DailyReportResponse daily(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getDaily(user, date);
    }

    // Any date within the target week; the report returns the full Monday-Sunday week.
    @GetMapping("/weekly")
    public WeeklyReportResponse weekly(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getWeekly(user, date != null ? date : LocalDate.now());
    }

    @GetMapping("/monthly")
    public MonthlyReportResponse monthly(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        YearMonth ym = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        return reportService.getMonthly(user, ym.getYear(), ym.getMonthValue());
    }

    @GetMapping("/category-analysis")
    public CategoryAnalysisResponse categoryAnalysis(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return reportService.getCategoryAnalysis(user, start, end);
    }

    @GetMapping("/income-vs-expense")
    public IncomeVsExpenseResponse incomeVsExpense(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return reportService.getIncomeVsExpense(user, start, end);
    }

    // Doubles as both "monthly spending comparison" (5.3) and "spending trends" (4.7) -
    // a trend is just this series charted. Defaults to the last 6 months.
    @GetMapping("/monthly-comparison")
    public List<MonthlyComparisonEntry> monthlyComparison(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "6") int months) {
        return reportService.getMonthlyComparison(user, months);
    }
}
