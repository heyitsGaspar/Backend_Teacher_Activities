package com.example.children_activities.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}