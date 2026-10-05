package com.example.children_activities.activities.entity;

import com.example.children_activities.subjects.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nombre o título de la actividad.
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * Fecha en la que se realiza la actividad.
     */
    @Column(nullable = false)
    private LocalDate activityDate;

    /**
     * Asignatura a la que pertenece la actividad.
     *
     * La asignatura pertenece a un maestro,
     * por lo tanto podemos conocer indirectamente
     * quién es el propietario de la actividad.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /**
     * Indica si esta es la actividad actualmente activa.
     *
     * Solo debe existir una actividad activa
     * por maestro.
     */
    @Column(nullable = false)
    private boolean active;

    /**
     * Fecha de creación de la actividad.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}