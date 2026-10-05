package com.example.children_activities.activities.service;

import com.example.children_activities.activities.dto.ActivityResponse;
import com.example.children_activities.activities.dto.CreateActivityRequest;
import com.example.children_activities.activities.entity.Activity;
import com.example.children_activities.activities.repository.ActivityRepository;
import com.example.children_activities.auth.security.CurrentUserService;
import com.example.children_activities.exception.ActivityNotFoundException;
import com.example.children_activities.exception.SubjectNotFoundException;
import com.example.children_activities.subjects.entity.Subject;
import com.example.children_activities.subjects.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final SubjectRepository subjectRepository;
    private final CurrentUserService currentUserService;

    public ActivityService(
            ActivityRepository activityRepository,
            SubjectRepository subjectRepository,
            CurrentUserService currentUserService
    ) {
        this.activityRepository = activityRepository;
        this.subjectRepository = subjectRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Crea una nueva actividad.
     *
     * La asignatura debe pertenecer al maestro
     * actualmente autenticado.
     */
    @Transactional
    public ActivityResponse create(
            CreateActivityRequest request
    ) {

        UUID teacherId = currentUserService.getCurrentUserId();

        /*
         * Buscamos la asignatura verificando también
         * que pertenezca al maestro autenticado.
         */
        Subject subject = subjectRepository
                .findByIdAndTeacherId(
                        request.subjectId(),
                        teacherId
                )
                .orElseThrow(() ->
                        new SubjectNotFoundException(
                                "Asignatura no encontrada"
                        )
                );

        /*
         * Una actividad nueva comienza
         * desactivada.
         */
        Activity activity = Activity.builder()
                .subject(subject)
                .title(request.title())
                .activityDate(request.activityDate())
                .active(false)
                .build();

        Activity savedActivity =
                activityRepository.save(activity);

        return ActivityResponse.fromEntity(savedActivity);
    }

    /**
     * Obtiene únicamente las actividades
     * pertenecientes al maestro autenticado.
     *
     * Utiliza paginación.
     */
    @Transactional(readOnly = true)
    public Page<ActivityResponse> findAll(
            Pageable pageable
    ) {

        UUID teacherId =
                currentUserService.getCurrentUserId();

        return activityRepository
                .findBySubjectTeacherId(
                        teacherId,
                        pageable
                )
                .map(ActivityResponse::fromEntity);
    }

    /**
     * Busca una actividad por ID.
     *
     * La actividad debe pertenecer al maestro
     * actualmente autenticado.
     */
    @Transactional(readOnly = true)
    public ActivityResponse findById(
            UUID id
    ) {

        UUID teacherId =
                currentUserService.getCurrentUserId();

        Activity activity = activityRepository
                .findByIdAndSubjectTeacherId(
                        id,
                        teacherId
                )
                .orElseThrow(() ->
                        new ActivityNotFoundException(
                                "Actividad no encontrada"
                        )
                );

        return ActivityResponse.fromEntity(activity);
    }

    /**
     * Actualiza una actividad.
     *
     * Tanto la actividad actual como la nueva asignatura
     * deben pertenecer al maestro autenticado.
     */
    @Transactional
    public ActivityResponse update(
            UUID id,
            CreateActivityRequest request
    ) {

        UUID teacherId =
                currentUserService.getCurrentUserId();

        /*
         * Primero verificamos que la actividad
         * pertenezca al maestro.
         */
        Activity activity = activityRepository
                .findByIdAndSubjectTeacherId(
                        id,
                        teacherId
                )
                .orElseThrow(() ->
                        new ActivityNotFoundException(
                                "Actividad no encontrada"
                        )
                );

        /*
         * Después verificamos que la nueva asignatura
         * también pertenezca al mismo maestro.
         */
        Subject subject = subjectRepository
                .findByIdAndTeacherId(
                        request.subjectId(),
                        teacherId
                )
                .orElseThrow(() ->
                        new SubjectNotFoundException(
                                "Asignatura no encontrada"
                        )
                );

        activity.setSubject(subject);
        activity.setTitle(request.title());
        activity.setActivityDate(request.activityDate());

        Activity updatedActivity =
                activityRepository.save(activity);

        return ActivityResponse.fromEntity(updatedActivity);
    }

    /**
     * Elimina una actividad únicamente si
     * pertenece al maestro autenticado.
     */
    @Transactional
    public void delete(
            UUID id
    ) {

        UUID teacherId =
                currentUserService.getCurrentUserId();

        Activity activity = activityRepository
                .findByIdAndSubjectTeacherId(
                        id,
                        teacherId
                )
                .orElseThrow(() ->
                        new ActivityNotFoundException(
                                "Actividad no encontrada"
                        )
                );

        activityRepository.delete(activity);
    }

    /**
     * Activa una actividad.
     *
     * Primero verifica que la actividad pertenezca
     * al maestro autenticado.
     *
     * Después desactiva la actividad que actualmente
     * esté activa para ese mismo maestro.
     */
    @Transactional
    public ActivityResponse activate(
            UUID id
    ) {

        UUID teacherId =
                currentUserService.getCurrentUserId();

        /*
         * Verificamos que la actividad pertenezca
         * al maestro autenticado.
         */
        Activity activity = activityRepository
                .findByIdAndSubjectTeacherId(
                        id,
                        teacherId
                )
                .orElseThrow(() ->
                        new ActivityNotFoundException(
                                "Actividad no encontrada"
                        )
                );

        /*
         * Buscamos únicamente la actividad activa
         * de este maestro.
         */
        activityRepository
                .findByActiveTrueAndSubjectTeacherId(
                        teacherId
                )
                .ifPresent(activeActivity -> {

                    /*
                     * Si ya estaba activa la misma actividad,
                     * no es necesario modificar otra.
                     */
                    if (!activeActivity.getId().equals(activity.getId())) {

                        activeActivity.setActive(false);

                        activityRepository.save(activeActivity);
                    }
                });

        /*
         * Activamos la actividad seleccionada.
         */
        activity.setActive(true);

        Activity activatedActivity =
                activityRepository.save(activity);

        return ActivityResponse.fromEntity(
                activatedActivity
        );
    }

    /**
     * Obtiene la actividad actualmente activa
     * del maestro autenticado.
     */
    @Transactional(readOnly = true)
    public ActivityResponse findActive() {

        UUID teacherId =
                currentUserService.getCurrentUserId();

        Activity activity = activityRepository
                .findByActiveTrueAndSubjectTeacherId(
                        teacherId
                )
                .orElseThrow(() ->
                        new ActivityNotFoundException(
                                "No hay ninguna actividad activa"
                        )
                );

        return ActivityResponse.fromEntity(activity);
    }
}