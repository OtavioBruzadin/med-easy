package com.example.med_easy.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}