package com.example.children_activities.children.repository;

import com.example.children_activities.children.entity.Child;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChildRepository extends JpaRepository<Child, UUID> {

    boolean existsByCode(String code);

    /**
     * Busca un alumno por su ID asegurándose
     * de que pertenece al maestro indicado.
     */
    Optional<Child> findByIdAndTeacherId(
            UUID childId,
            UUID teacherId
    );

    /**
     * Obtiene únicamente los alumnos del maestro indicado
     * utilizando paginación.
     */
    Page<Child> findByTeacherId(
            UUID teacherId,
            Pageable pageable
    );

    /**
     * Busca un alumno mediante su código QR,
     * pero únicamente si pertenece al maestro indicado.
     */
    Optional<Child> findByCodeAndTeacherId(
            String code,
            UUID teacherId
    );
}