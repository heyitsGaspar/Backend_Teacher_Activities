package com.example.children_activities.activities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateActivityRequest(

        @NotNull(message = "La asignatura es obligatoria")
        UUID subjectId,

        @NotBlank(message = "El título de la actividad es obligatorio")
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String title,

        @NotNull(message = "La fecha de la actividad es obligatoria")
        LocalDate activityDate,

        Boolean activate

) {
}