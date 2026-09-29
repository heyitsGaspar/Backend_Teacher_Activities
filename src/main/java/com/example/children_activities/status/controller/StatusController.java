package com.example.children_activities.status.controller;

import com.example.children_activities.status.dto.StatusResponse;
import com.example.children_activities.status.service.StatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller encargado de exponer
 * la API general del estado de entregas.
 */
@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final StatusService statusService;

    /**
     * Constructor donde Spring inyecta
     * nuestro StatusService.
     */
    public StatusController(
            StatusService statusService
    ) {
        this.statusService = statusService;
    }

    /**
     * Obtiene el estado de los alumnos
     * frente a las actividades.
     *
     * Endpoint:
     *
     * GET /api/status
     *
     * También acepta filtros:
     *
     * GET /api/status?subjectId=...
     *
     * GET /api/status?activityId=...
     *
     * GET /api/status?studentName=Manuel
     *
     * GET /api/status?submitted=true
     *
     * GET /api/status?submitted=false
     *
     * Los filtros pueden combinarse.
     */
    @GetMapping
    public List<StatusResponse> findStatus(

            /*
             * UUID de la asignatura.
             *
             * required = false significa que
             * el parámetro es opcional.
             */
            @RequestParam(required = false)
            UUID subjectId,

            /*
             * UUID de una actividad específica.
             */
            @RequestParam(required = false)
            UUID activityId,

            /*
             * Nombre del alumno que queremos buscar.
             */
            @RequestParam(required = false)
            String studentName,

            /*
             * true  = solamente entregados
             * false = solamente pendientes
             */
            @RequestParam(required = false)
            Boolean submitted
    ) {

        /*
         * Enviamos todos los filtros al Service.
         *
         * El Controller no contiene la lógica
         * de negocio.
         *
         * Esa responsabilidad pertenece al Service.
         */
        return statusService.findStatus(
                subjectId,
                activityId,
                studentName,
                submitted
        );
    }
}