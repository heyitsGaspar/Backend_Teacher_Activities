package com.example.children_activities.children.dto;

import com.example.children_activities.children.entity.Child;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChildResponse(
        UUID id,
        String name,
        String code,
        LocalDateTime createdAt
) {
    public static ChildResponse fromEntity(Child child) {
        return new ChildResponse(
                child.getId(),
                child.getName(),
                child.getCode(),
                child.getCreatedAt()
        );
    }
}
