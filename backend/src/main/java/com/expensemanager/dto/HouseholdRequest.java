package com.expensemanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HouseholdRequest(
        @NotBlank(message = "Household name is required")
        @Size(max = 100, message = "Household name cannot exceed 100 characters")
        String name
) {}
