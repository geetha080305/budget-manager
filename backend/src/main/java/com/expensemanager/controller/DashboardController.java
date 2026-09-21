package com.expensemanager.controller;

import com.expensemanager.dto.DashboardResponse;
import com.expensemanager.model.User;
import com.expensemanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Defaults to the current month when year/month aren't passed.
    @GetMapping
    public DashboardResponse getDashboard(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return dashboardService.getDashboard(user, year, month);
    }
}
