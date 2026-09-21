package com.expensemanager.service;

import com.expensemanager.dto.BudgetRequest;
import com.expensemanager.dto.BudgetResponse;
import com.expensemanager.model.Budget;
import com.expensemanager.model.User;
import com.expensemanager.repository.BudgetRepository;
import com.expensemanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional
    public BudgetResponse create(User user, BudgetRequest request) {
        Budget budget = Budget.builder()
                .user(user)
                .category(request.category())
                .amount(request.amount())
                .budgetYear(request.budgetYear())
                .budgetMonth(request.budgetMonth())
                .build();
        try {
            budget = budgetRepository.save(budget);
        } catch (DataIntegrityViolationException e) {
            // Hits the unique (user, category, year, month) constraint.
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A budget already exists for that category and month - update it instead");
        }
        return toResponse(budget);
    }

    public List<BudgetResponse> list(User user, Integer year, Integer month) {
        int y = year != null ? year : LocalDate.now().getYear();
        int m = month != null ? month : LocalDate.now().getMonthValue();
        return budgetRepository.findByUserIdAndBudgetYearAndBudgetMonth(user.getId(), y, m)
                .stream().map(this::toResponse).toList();
    }

    public BudgetResponse get(User user, Long id) {
        return toResponse(findOwned(user, id));
    }

    @Transactional
    public BudgetResponse update(User user, Long id, BudgetRequest request) {
        Budget budget = findOwned(user, id);
        budget.setCategory(request.category());
        budget.setAmount(request.amount());
        budget.setBudgetYear(request.budgetYear());
        budget.setBudgetMonth(request.budgetMonth());
        return toResponse(budget);
    }

    @Transactional
    public void delete(User user, Long id) {
        budgetRepository.delete(findOwned(user, id));
    }

    private Budget findOwned(User user, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found"));
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found");
        }
        return budget;
    }

    private BudgetResponse toResponse(Budget budget) {
        YearMonth ym = YearMonth.of(budget.getBudgetYear(), budget.getBudgetMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal spent = budget.getCategory() != null
                ? expenseRepository.sumByUserIdAndCategoryAndDateBetween(
                        budget.getUser().getId(), budget.getCategory(), start, end)
                : expenseRepository.sumByUserIdAndDateBetween(budget.getUser().getId(), start, end);

        BigDecimal remaining = budget.getAmount().subtract(spent);
        boolean exceeded = remaining.signum() < 0;

        return new BudgetResponse(
                budget.getId(), budget.getCategory(), budget.getAmount(),
                budget.getBudgetYear(), budget.getBudgetMonth(),
                spent, exceeded ? BigDecimal.ZERO : remaining, exceeded);
    }
}
