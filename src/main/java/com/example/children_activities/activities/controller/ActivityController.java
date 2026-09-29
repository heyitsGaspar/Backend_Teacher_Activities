package com.example.children_activities.activities.controller;

import com.example.children_activities.activities.dto.ActivityResponse;
import com.example.children_activities.activities.dto.CreateActivityRequest;
import com.example.children_activities.activities.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    // Endpoint to create a new activity
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse create(
            @Valid @RequestBody CreateActivityRequest request
    ) {
        return activityService.create(request);
    }

    // Endpoint to find all activities
    @GetMapping
    public List<ActivityResponse> findAll() {
        return activityService.findAll();
    }

    // Endpoint to find an activity by its ID
    @GetMapping("/{id}")
    public ActivityResponse findById(
            @PathVariable UUID id
    ) {
        return activityService.findById(id);
    }

    // Endpoint to update an activity
    @PutMapping("/{id}")
    public ActivityResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateActivityRequest request
    ) {
        return activityService.update(id, request);
    }

    // Endpoint to delete an activity
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        activityService.delete(id);
    }

    // Endpoint to activate an activity
    @PatchMapping("/{id}/activate")
    public ActivityResponse activate(
            @PathVariable UUID id
    ) {
        return activityService.activate(id);
    }

    // Endpoint to find the currently active activity
    @GetMapping("/active/current")
    public ActivityResponse findActive() {
        return activityService.findActive();
    }
}