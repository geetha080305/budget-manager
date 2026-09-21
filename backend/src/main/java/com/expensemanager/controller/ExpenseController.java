package com.expensemanager.controller;

import com.expensemanager.dto.ExpenseRequest;
import com.expensemanager.dto.ExpenseResponse;
import com.expensemanager.model.ExpenseCategory;
import com.expensemanager.model.User;
import com.expensemanager.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(user, request));
    }

    // Filters per Section 4.3/5.4: date range and/or category.
    // (Amount/payment-method filtering can be added the same way if the frontend needs it.)
    @GetMapping
    public List<ExpenseResponse> list(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) ExpenseCategory category) {
        return expenseService.list(user, start, end, category);
    }

    @GetMapping("/{id}")
    public ExpenseResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return expenseService.get(user, id);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        expenseService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
