package com.example.children_activities.submissions.service;

import com.example.children_activities.activities.entity.Activity;
import com.example.children_activities.activities.repository.ActivityRepository;
import com.example.children_activities.children.entity.Child;
import com.example.children_activities.children.repository.ChildRepository;
import com.example.children_activities.submissions.dto.CreateSubmissionRequest;
import com.example.children_activities.submissions.dto.SubmissionResponse;
import com.example.children_activities.submissions.entity.Submission;
import com.example.children_activities.submissions.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ChildRepository childRepository;
    private final ActivityRepository activityRepository;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            ChildRepository childRepository,
            ActivityRepository activityRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public SubmissionResponse create(CreateSubmissionRequest request) {

        Child child = childRepository.findByCode(request.code())
                .orElseThrow(() ->
                        new RuntimeException("Alumno no encontrado")
                );

        Activity activity = activityRepository.findById(request.activityId())
                .orElseThrow(() ->
                        new RuntimeException("Actividad no encontrada")
                );

        boolean alreadySubmitted =
                submissionRepository.existsByChildIdAndActivityId(
                        child.getId(),
                        activity.getId()
                );

        if (alreadySubmitted) {
            throw new RuntimeException(
                    "El alumno ya entregó esta actividad"
            );
        }

        Submission submission = Submission.builder()
                .child(child)
                .activity(activity)
                .build();

        Submission savedSubmission =
                submissionRepository.save(submission);

        return SubmissionResponse.fromEntity(savedSubmission);
    }

    @Transactional
    public void delete(UUID id) {

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Entrega no encontrada")
                );

        submissionRepository.delete(submission);
    }
}