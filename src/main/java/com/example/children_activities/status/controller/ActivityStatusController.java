package com.example.children_activities.status.controller;

import com.example.children_activities.status.dto.StatusResponse;
import com.example.children_activities.status.service.StatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller encargado de consultar
 * el estado de una actividad específica.
 *
 * Ejemplo:
 *
 * GET /api/activities/{id}/status
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityStatusController {

    private final StatusService statusService;

    /**
     * Constructor donde Spring inyecta
     * el StatusService.
     */
    public ActivityStatusController(
            StatusService statusService
    ) {
        this.statusService = statusService;
    }

    /**
     * Obtiene todos los alumnos y muestra
     * si entregaron o tienen pendiente
     * una actividad específica.
     *
     * Ejemplo:
     *
     * GET /api/activities/123/status
     *
     * Resultado conceptual:
     *
     * Juan    -> Entregado
     * Manuel  -> Pendiente
     * Abel    -> Entregado
     * Marcos  -> Entregado
     */
    @GetMapping("/{id}/status")
    public List<StatusResponse> findByActivity(
            @PathVariable UUID id
    ) {

        /*
         * Mandamos el ID de la actividad
         * al Service.
         */
        return statusService.findByActivityId(id);
    }
}