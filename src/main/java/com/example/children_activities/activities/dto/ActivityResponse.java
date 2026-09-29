package com.example.children_activities.activities.dto;

import com.example.children_activities.activities.entity.Activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        UUID subjectId,
        String subjectName,
        String title,
        LocalDate activityDate,
        boolean active,
        LocalDateTime createdAt
) {

    public static ActivityResponse fromEntity(Activity activity) {

        return new ActivityResponse(
                activity.getId(),
                activity.getSubject().getId(),
                activity.getSubject().getName(),
                activity.getTitle(),
                activity.getActivityDate(),
                activity.isActive(),
                activity.getCreatedAt()
        );
    }
}