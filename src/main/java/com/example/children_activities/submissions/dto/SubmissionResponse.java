package com.example.children_activities.submissions.dto;

import com.example.children_activities.submissions.entity.Submission;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        UUID childId,
        String childName,
        String childCode,
        UUID activityId,
        String activityTitle,
        LocalDateTime createdAt
) {

    public static SubmissionResponse fromEntity(Submission submission) {

        return new SubmissionResponse(
                submission.getId(),
                submission.getChild().getId(),
                submission.getChild().getName(),
                submission.getChild().getCode(),
                submission.getActivity().getId(),
                submission.getActivity().getTitle(),
                submission.getCreatedAt()
        );
    }
}