package com.example.children_activities.submissions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSubmissionRequest(

        @NotBlank(message = "El código del alumno es obligatorio")
        String code,

        @NotNull(message = "La actividad es obligatoria")
        UUID activityId

) {
}