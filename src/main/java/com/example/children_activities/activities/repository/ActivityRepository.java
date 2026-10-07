package com.example.children_activities.activities.repository;

import com.example.children_activities.activities.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    /**
     * Obtiene únicamente las actividades
     * pertenecientes al maestro indicado.
     *
     * La relación es:
     *
     * Activity -> Subject -> Teacher
     */
    @EntityGraph(attributePaths = "subject")
    Page<Activity> findBySubjectTeacherId(
            UUID teacherId,
            Pageable pageable
    );

    /**
     * Busca una actividad por ID,
     * verificando que la asignatura de la actividad
     * pertenezca al maestro indicado.
     */
    Optional<Activity> findByIdAndSubjectTeacherId(
            UUID activityId,
            UUID teacherId
    );

    /**
     * Busca la actividad activa del maestro indicado.
     *
     * De esta manera un maestro nunca puede
     * obtener la actividad activa de otro maestro.
     */
    @EntityGraph(attributePaths = "subject")
    Optional<Activity> findByActiveTrueAndSubjectTeacherId(
            UUID teacherId
    );
}