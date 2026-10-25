package com.expensemanager.controller;

import com.expensemanager.dto.HouseholdRequest;
import com.expensemanager.dto.HouseholdResponse;
import com.expensemanager.dto.JoinHouseholdRequest;
import com.expensemanager.model.User;
import com.expensemanager.service.HouseholdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/household")
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdService householdService;

    @GetMapping
    public ResponseEntity<HouseholdResponse> getMyHousehold(@AuthenticationPrincipal User user) {
        HouseholdResponse response = householdService.getMyHousehold(user);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<HouseholdResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody HouseholdRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(householdService.createHousehold(user, request));
    }

    @PostMapping("/join")
    public ResponseEntity<HouseholdResponse> join(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody JoinHouseholdRequest request) {
        return ResponseEntity.ok(householdService.joinHousehold(user, request));
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leave(@AuthenticationPrincipal User user) {
        householdService.leaveHousehold(user);
        return ResponseEntity.noContent().build();
    }
}
