package com.example.children_activities.subjects.controller;

import com.example.children_activities.subjects.dto.CreateSubjectRequest;
import com.example.children_activities.subjects.dto.SubjectResponse;
import com.example.children_activities.subjects.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * Crea una nueva asignatura para
     * el maestro autenticado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse create(
            @Valid @RequestBody CreateSubjectRequest request
    ) {
        return subjectService.create(request);
    }

    /**
     * Obtiene únicamente las asignaturas
     * del maestro autenticado.
     *
     * Ejemplo:
     * /api/subjects?page=0&size=10
     */
    @GetMapping
    public Page<SubjectResponse> findAll(
            Pageable pageable
    ) {
        return subjectService.findAll(pageable);
    }

    /**
     * Obtiene una asignatura por ID,
     * siempre que pertenezca al maestro autenticado.
     */
    @GetMapping("/{id}")
    public SubjectResponse findById(
            @PathVariable UUID id
    ) {
        return subjectService.findById(id);
    }

    /**
     * Actualiza una asignatura perteneciente
     * al maestro autenticado.
     */
    @PutMapping("/{id}")
    public SubjectResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateSubjectRequest request
    ) {
        return subjectService.update(id, request);
    }

    /**
     * Elimina una asignatura perteneciente
     * al maestro autenticado.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        subjectService.delete(id);
    }
}