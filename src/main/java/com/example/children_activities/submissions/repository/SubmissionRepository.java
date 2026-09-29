package com.example.children_activities.submissions.repository;

import com.example.children_activities.submissions.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository
        extends JpaRepository<Submission, UUID> {

    boolean existsByChildIdAndActivityId(
            UUID childId,
            UUID activityId
    );

    Optional<Submission> findByChildIdAndActivityId(
            UUID childId,
            UUID activityId
    );
}