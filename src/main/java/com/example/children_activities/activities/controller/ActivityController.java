package com.example.children_activities.activities.controller;

import com.example.children_activities.activities.dto.ActivityResponse;
import com.example.children_activities.activities.dto.CreateActivityRequest;
import com.example.children_activities.activities.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(
            ActivityService activityService
    ) {
        this.activityService = activityService;
    }

    /**
     * Crea una nueva actividad
     * para el maestro autenticado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse create(
            @Valid @RequestBody CreateActivityRequest request
    ) {
        return activityService.create(request);
    }

    /**
     * Obtiene las actividades del maestro autenticado.
     *
     * Permite paginación:
     *
     * /api/activities?page=0&size=10
     */
    @GetMapping
    public Page<ActivityResponse> findAll(
            Pageable pageable
    ) {
        return activityService.findAll(pageable);
    }

    /**
     * Obtiene una actividad por ID,
     * siempre que pertenezca al maestro autenticado.
     */
    @GetMapping("/{id}")
    public ActivityResponse findById(
            @PathVariable UUID id
    ) {
        return activityService.findById(id);
    }

    /**
     * Actualiza una actividad
     * perteneciente al maestro autenticado.
     */
    @PutMapping("/{id}")
    public ActivityResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateActivityRequest request
    ) {
        return activityService.update(id, request);
    }

    /**
     * Elimina una actividad
     * perteneciente al maestro autenticado.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        activityService.delete(id);
    }

    /**
     * Activa una actividad.
     *
     * Si existe otra actividad activa
     * del mismo maestro, se desactiva.
     */
    @PatchMapping("/{id}/activate")
    public ActivityResponse activate(
            @PathVariable UUID id
    ) {
        return activityService.activate(id);
    }

    /**
     * Obtiene la actividad actualmente activa
     * del maestro autenticado.
     */
    @GetMapping("/active/current")
    public ActivityResponse findActive() {
        return activityService.findActive();
    }
}