package com.expensemanager.controller;

import com.expensemanager.dto.ContributionRequest;
import com.expensemanager.dto.SavingsGoalRequest;
import com.expensemanager.dto.SavingsGoalResponse;
import com.expensemanager.model.User;
import com.expensemanager.service.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings-goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SavingsGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsGoalService.create(user, request));
    }

    @GetMapping
    public List<SavingsGoalResponse> list(@AuthenticationPrincipal User user) {
        return savingsGoalService.list(user);
    }

    @GetMapping("/{id}")
    public SavingsGoalResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return savingsGoalService.get(user, id);
    }

    @PutMapping("/{id}")
    public SavingsGoalResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalRequest request) {
        return savingsGoalService.update(user, id, request);
    }

    // Adds to the saved amount (e.g. "I put ₹2,000 toward this goal today").
    @PostMapping("/{id}/contribute")
    public SavingsGoalResponse contribute(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody ContributionRequest request) {
        return savingsGoalService.contribute(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        savingsGoalService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
