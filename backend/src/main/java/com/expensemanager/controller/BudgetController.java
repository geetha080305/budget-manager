package com.expensemanager.controller;

import com.expensemanager.dto.BudgetRequest;
import com.expensemanager.dto.BudgetResponse;
import com.expensemanager.model.User;
import com.expensemanager.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(user, request));
    }

    // Defaults to the current year/month when neither is passed.
    @GetMapping
    public List<BudgetResponse> list(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return budgetService.list(user, year, month);
    }

    @GetMapping("/{id}")
    public BudgetResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return budgetService.get(user, id);
    }

    @PutMapping("/{id}")
    public BudgetResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        return budgetService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        budgetService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
