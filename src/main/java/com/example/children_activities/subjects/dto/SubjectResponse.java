package com.example.children_activities.subjects.dto;

import com.example.children_activities.subjects.entity.Subject;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubjectResponse(
        UUID id,
        String name,
        LocalDateTime createdAt
) {
    public static SubjectResponse fromEntity(Subject subject) {
        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getCreatedAt()
        );
    }
}