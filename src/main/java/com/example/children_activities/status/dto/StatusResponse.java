package com.example.children_activities.status.dto;

import com.example.children_activities.submissions.entity.Submission;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO utilizado para representar el estado
 * de un alumno frente a una actividad.
 *
 * IMPORTANTE:
 *
 * Submission representa solamente una entrega que existe.
 *
 * StatusResponse representa el estado:
 *
 * - Entregado
 * - Pendiente
 *
 * Por eso este DTO también puede representar
 * una combinación alumno + actividad que todavía
 * NO tiene una Submission.
 */
public record StatusResponse(

        // Información del alumno
        UUID studentId,
        String studentName,
        String studentCode,

        // Información de la asignatura
        UUID subjectId,
        String subjectName,

        // Información de la actividad
        UUID activityId,
        String activityTitle,

        // true  = Entregado
        // false = Pendiente
        boolean submitted,

        // Fecha y hora de entrega.
        // Será null cuando esté pendiente.
        LocalDateTime submittedAt
) {

    /**
     * Convierte una Submission existente
     * en un StatusResponse.
     *
     * Este método se utiliza cuando encontramos
     * una entrega para el alumno y la actividad.
     *
     * @param submission entrega existente
     * @return StatusResponse con submitted = true
     */
    public static StatusResponse submitted(Submission submission) {

        return new StatusResponse(

                // Datos del alumno
                submission.getChild().getId(),
                submission.getChild().getName(),
                submission.getChild().getCode(),

                // Datos de la asignatura
                submission.getActivity().getSubject().getId(),
                submission.getActivity().getSubject().getName(),

                // Datos de la actividad
                submission.getActivity().getId(),
                submission.getActivity().getTitle(),

                // Si existe una Submission,
                // significa que el alumno entregó.
                true,

                // Fecha de entrega
                submission.getCreatedAt()
        );
    }
}