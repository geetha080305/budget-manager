package com.expensemanager.controller;

import com.expensemanager.dto.IncomeRequest;
import com.expensemanager.dto.IncomeResponse;
import com.expensemanager.model.User;
import com.expensemanager.service.IncomeService;
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
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody IncomeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incomeService.create(user, request));
    }

    @GetMapping
    public List<IncomeResponse> list(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return incomeService.list(user, start, end);
    }

    @GetMapping("/{id}")
    public IncomeResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return incomeService.get(user, id);
    }

    @PutMapping("/{id}")
    public IncomeResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        return incomeService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        incomeService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
