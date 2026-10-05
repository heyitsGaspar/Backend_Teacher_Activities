package com.example.children_activities.submissions.repository;

import com.example.children_activities.submissions.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository
        extends JpaRepository<Submission, UUID> {

    /**
     * Verifica si un alumno ya entregó
     * una actividad.
     */
    boolean existsByChildIdAndActivityId(
            UUID childId,
            UUID activityId
    );

    /**
     * Busca una entrega específica de un alumno
     * para una actividad.
     */
    Optional<Submission> findByChildIdAndActivityId(
            UUID childId,
            UUID activityId
    );

    /**
     * Busca una entrega verificando que la actividad
     * pertenezca al maestro indicado.
     */
    Optional<Submission> findByIdAndActivitySubjectTeacherId(
            UUID submissionId,
            UUID teacherId
    );
}