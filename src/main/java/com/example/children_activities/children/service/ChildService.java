package com.example.children_activities.children.service;

import com.example.children_activities.auth.security.CurrentUserService;
import com.example.children_activities.children.dto.ChildResponse;
import com.example.children_activities.children.dto.CreateChildRequest;
import com.example.children_activities.children.entity.Child;
import com.example.children_activities.children.repository.ChildRepository;
import com.example.children_activities.exception.ChildNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChildService {

    private final ChildRepository childRepository;
    private final CurrentUserService currentUserService;

    public ChildService(
            ChildRepository childRepository,
            CurrentUserService currentUserService
    ) {
        this.childRepository = childRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Crea un nuevo alumno y lo asigna
     * al maestro actualmente autenticado.
     */
    @Transactional
    public ChildResponse create(CreateChildRequest request) {

        String code = generateUniqueCode();

        Child child = Child.builder()
                .name(request.name())
                .code(code)
                .teacher(currentUserService.getCurrentUser())
                .build();

        Child savedChild = childRepository.save(child);

        return ChildResponse.fromEntity(savedChild);
    }

    /**
     * Obtiene los alumnos del maestro autenticado.
     *
     * La consulta se realiza directamente en la base de datos
     * utilizando el teacherId y paginación.
     */
    @Transactional(readOnly = true)
    public Page<ChildResponse> findAll(Pageable pageable) {

        UUID teacherId = currentUserService.getCurrentUserId();

        return childRepository
                .findByTeacherId(teacherId, pageable)
                .map(ChildResponse::fromEntity);
    }

    /**
     * Busca un alumno por ID, pero únicamente si pertenece
     * al maestro autenticado.
     */
    @Transactional(readOnly = true)
    public ChildResponse findById(UUID id) {

        UUID teacherId = currentUserService.getCurrentUserId();

        Child child = childRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() ->
                        new ChildNotFoundException("Alumno no encontrado")
                );

        return ChildResponse.fromEntity(child);
    }

    /**
     * Busca un alumno mediante su código QR,
     * pero únicamente si pertenece al maestro autenticado.
     */
    @Transactional(readOnly = true)
    public ChildResponse findByCode(String code) {

        UUID teacherId = currentUserService.getCurrentUserId();

        Child child = childRepository
                .findByCodeAndTeacherId(code, teacherId)
                .orElseThrow(() ->
                        new ChildNotFoundException("Alumno no encontrado")
                );

        return ChildResponse.fromEntity(child);
    }

    /**
     * Genera un código único para el alumno.
     */
    private String generateUniqueCode() {

        String code;

        do {

            code = "ALU-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 5)
                    .toUpperCase();

        } while (childRepository.existsByCode(code));

        return code;
    }
}