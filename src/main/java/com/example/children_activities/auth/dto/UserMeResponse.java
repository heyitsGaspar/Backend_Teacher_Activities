package com.example.children_activities.auth.dto;

import java.util.UUID;

public record UserMeResponse(
        UUID id,
        String name,
        String email
) {
}