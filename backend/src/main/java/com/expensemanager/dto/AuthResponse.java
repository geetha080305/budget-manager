package com.expensemanager.dto;

public record AuthResponse(
        String token,
        String tokenType,
        String username,
        long expiresInMs
) {
    public static AuthResponse bearer(String token, String username, long expiresInMs) {
        return new AuthResponse(token, "Bearer", username, expiresInMs);
    }
}
