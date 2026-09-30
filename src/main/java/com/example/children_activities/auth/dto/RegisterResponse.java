package com.example.children_activities.auth.dto;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String name,
        String email
) {
}