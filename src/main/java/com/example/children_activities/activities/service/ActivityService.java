package com.example.children_activities.activities.service;

import com.example.children_activities.activities.dto.ActivityResponse;
import com.example.children_activities.activities.dto.CreateActivityRequest;
import com.example.children_activities.activities.entity.Activity;
import com.example.children_activities.activities.repository.ActivityRepository;
import com.example.children_activities.subjects.entity.Subject;
import com.example.children_activities.subjects.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final SubjectRepository subjectRepository;

    public ActivityService(
            ActivityRepository activityRepository,
            SubjectRepository subjectRepository
    ) {
        this.activityRepository = activityRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public ActivityResponse create(CreateActivityRequest request) {

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() ->
                        new RuntimeException("Asignatura no encontrada")
                );

        Activity activity = Activity.builder()
                .subject(subject)
                .title(request.title())
                .activityDate(request.activityDate())
                .active(false)
                .build();

        Activity savedActivity = activityRepository.save(activity);

        return ActivityResponse.fromEntity(savedActivity);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> findAll() {

        return activityRepository.findAll()
                .stream()
                .map(ActivityResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponse findById(UUID id) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Actividad no encontrada")
                );

        return ActivityResponse.fromEntity(activity);
    }

    @Transactional
    public ActivityResponse update(
            UUID id,
            CreateActivityRequest request
    ) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Actividad no encontrada")
                );

        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() ->
                        new RuntimeException("Asignatura no encontrada")
                );

        activity.setSubject(subject);
        activity.setTitle(request.title());
        activity.setActivityDate(request.activityDate());

        Activity updatedActivity = activityRepository.save(activity);

        return ActivityResponse.fromEntity(updatedActivity);
    }

    @Transactional
    public void delete(UUID id) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Actividad no encontrada")
                );

        activityRepository.delete(activity);
    }

    @Transactional
    public ActivityResponse activate(UUID id) {

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Actividad no encontrada")
                );

        activityRepository.findByActiveTrue()
                .ifPresent(activeActivity -> {
                    activeActivity.setActive(false);
                    activityRepository.save(activeActivity);
                });

        activity.setActive(true);

        Activity activatedActivity = activityRepository.save(activity);

        return ActivityResponse.fromEntity(activatedActivity);
    }

    @Transactional(readOnly = true)
    public ActivityResponse findActive() {

        Activity activity = activityRepository.findByActiveTrue()
                .orElseThrow(() ->
                        new RuntimeException("No hay ninguna actividad activa")
                );

        return ActivityResponse.fromEntity(activity);
    }
}