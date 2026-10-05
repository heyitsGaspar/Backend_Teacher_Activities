package com.example.children_activities.children.controller;

import com.example.children_activities.children.dto.ChildResponse;
import com.example.children_activities.children.dto.CreateChildRequest;
import com.example.children_activities.children.service.ChildService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildService childService;

    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    /**
     * Crea un nuevo alumno.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildResponse create(
            @RequestBody @Valid CreateChildRequest request
    ) {
        return childService.create(request);
    }

    /**
     * Obtiene los alumnos del maestro autenticado.
     *
     * Permite utilizar paginación mediante:
     *
     * /api/children?page=0&size=10
     */
    @GetMapping
    public Page<ChildResponse> findAll(Pageable pageable) {
        return childService.findAll(pageable);
    }

    /**
     * Obtiene un alumno por ID.
     *
     * El servicio verifica que pertenezca
     * al maestro autenticado.
     */
    @GetMapping("/{id}")
    public ChildResponse findById(@PathVariable UUID id) {
        return childService.findById(id);
    }

    /**
     * Busca un alumno mediante su código QR.
     *
     * El servicio verifica que el alumno
     * pertenezca al maestro autenticado.
     */
    @GetMapping("/code/{code}")
    public ChildResponse findByCode(@PathVariable String code) {
        return childService.findByCode(code);
    }
}