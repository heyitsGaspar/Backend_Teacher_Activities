package com.example.children_activities.submissions.service;

import com.example.children_activities.activities.entity.Activity;
import com.example.children_activities.activities.repository.ActivityRepository;
import com.example.children_activities.auth.security.CurrentUserService;
import com.example.children_activities.children.entity.Child;
import com.example.children_activities.children.repository.ChildRepository;
import com.example.children_activities.exception.ActivityNotFoundException;
import com.example.children_activities.exception.ChildNotFoundException;
import com.example.children_activities.exception.DuplicateSubmissionException;
import com.example.children_activities.exception.SubmissionNotFoundException;
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
    private final CurrentUserService currentUserService;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            ChildRepository childRepository,
            ActivityRepository activityRepository,
            CurrentUserService currentUserService
    ) {
        this.submissionRepository = submissionRepository;
        this.childRepository = childRepository;
        this.activityRepository = activityRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Registra la entrega de un alumno
     * para una actividad.
     *
     * Tanto el alumno como la actividad
     * deben pertenecer al maestro autenticado.
     */
    @Transactional
    public SubmissionResponse create(
            CreateSubmissionRequest request
    ) {

        /*
         * Obtenemos el ID del maestro directamente
         * desde el usuario autenticado.
         */
        UUID teacherId =
                currentUserService.getCurrentUserId();

        /*
         * Buscamos al alumno mediante el código QR,
         * pero únicamente si pertenece al maestro actual.
         */
        Child child = childRepository
                .findByCodeAndTeacherId(
                        request.code(),
                        teacherId
                )
                .orElseThrow(() ->
                        new ChildNotFoundException(
                                "Alumno no encontrado"
                        )
                );

        /*
         * Buscamos la actividad verificando que
         * pertenezca al mismo maestro.
         *
         * Activity -> Subject -> Teacher
         */
        Activity activity = activityRepository
                .findByIdAndSubjectTeacherId(
                        request.activityId(),
                        teacherId
                )
                .orElseThrow(() ->
                        new ActivityNotFoundException(
                                "Actividad no encontrada"
                        )
                );

        /*
         * Verificamos si el alumno ya registró
         * esta actividad anteriormente.
         */
        boolean alreadySubmitted =
                submissionRepository
                        .existsByChildIdAndActivityId(
                                child.getId(),
                                activity.getId()
                        );

        if (alreadySubmitted) {

            throw new DuplicateSubmissionException(
                    "El alumno ya entregó esta actividad"
            );
        }

        /*
         * Creamos la entrega.
         */
        Submission submission = Submission.builder()
                .child(child)
                .activity(activity)
                .build();

        Submission savedSubmission =
                submissionRepository.save(submission);

        return SubmissionResponse.fromEntity(
                savedSubmission
        );
    }

    /**
     * Elimina una entrega.
     *
     * Solamente se puede eliminar una entrega
     * perteneciente al maestro autenticado.
     */
    @Transactional
    public void delete(UUID id) {

        UUID teacherId =
                currentUserService.getCurrentUserId();

        /*
         * Primero obtenemos la entrega.
         */
        Submission submission =
                submissionRepository.findById(id)
                        .orElseThrow(() ->
                                new SubmissionNotFoundException(
                                        "Entrega no encontrada"
                                )
                        );

        /*
         * Verificamos que la actividad asociada
         * a la entrega pertenezca al maestro actual.
         */
        UUID submissionTeacherId =
                submission.getActivity()
                        .getSubject()
                        .getTeacher()
                        .getId();

        if (!submissionTeacherId.equals(teacherId)) {

            throw new SubmissionNotFoundException(
                    "Entrega no encontrada"
            );
        }

        submissionRepository.delete(submission);
    }
}