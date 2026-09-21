package com.expensemanager.controller;

import com.expensemanager.dto.PaymentReminderRequest;
import com.expensemanager.dto.PaymentReminderResponse;
import com.expensemanager.model.User;
import com.expensemanager.service.PaymentReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-reminders")
@RequiredArgsConstructor
public class PaymentReminderController {

    private final PaymentReminderService reminderService;

    @PostMapping
    public ResponseEntity<PaymentReminderResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PaymentReminderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reminderService.create(user, request));
    }

    @GetMapping
    public List<PaymentReminderResponse> list(@AuthenticationPrincipal User user) {
        return reminderService.list(user);
    }

    @GetMapping("/{id}")
    public PaymentReminderResponse get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return reminderService.get(user, id);
    }

    @PutMapping("/{id}")
    public PaymentReminderResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody PaymentReminderRequest request) {
        return reminderService.update(user, id, request);
    }

    @PostMapping("/{id}/mark-paid")
    public PaymentReminderResponse markPaid(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return reminderService.markPaid(user, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        reminderService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
