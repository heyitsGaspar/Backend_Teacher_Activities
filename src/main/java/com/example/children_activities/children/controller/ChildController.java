package com.example.children_activities.children.controller;

import com.example.children_activities.children.dto.ChildResponse;
import com.example.children_activities.children.dto.CreateChildRequest;
import com.example.children_activities.children.service.ChildService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildService childService;

    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildResponse create(@RequestBody @Valid CreateChildRequest request) {
        return childService.create(request);
    }

    @GetMapping
    public List<ChildResponse> findAll() {
        return childService.findAll();
    }

    @GetMapping("/{id}")
    public ChildResponse findById(@PathVariable UUID id) {
        return childService.findById(id);
    }


}