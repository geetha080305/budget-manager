package com.expensemanager.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContributionRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Contribution must be greater than zero") BigDecimal amount
) {}
