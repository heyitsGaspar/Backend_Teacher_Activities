package com.example.children_activities.subjects.repository;

import com.example.children_activities.subjects.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    /**
     * Obtiene únicamente las asignaturas
     * pertenecientes al maestro indicado.
     */
    Page<Subject> findByTeacherId(
            UUID teacherId,
            Pageable pageable
    );

    /**
     * Busca una asignatura por ID,
     * verificando que pertenezca al maestro indicado.
     */
    Optional<Subject> findByIdAndTeacherId(
            UUID subjectId,
            UUID teacherId
    );

    /**
     * Verifica si el maestro ya tiene
     * una asignatura con ese nombre.
     */
    boolean existsByNameIgnoreCaseAndTeacherId(
            String name,
            UUID teacherId
    );
}