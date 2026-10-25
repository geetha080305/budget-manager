package com.expensemanager.dto;

import java.util.List;

public record HouseholdResponse(
        Long id,
        String name,
        String inviteCode,
        List<MemberSummary> members
) {
    public record MemberSummary(
            Long id,
            String username,
            String fullName
    ) {}
}
