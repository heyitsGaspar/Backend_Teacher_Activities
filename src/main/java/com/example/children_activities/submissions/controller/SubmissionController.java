package com.example.children_activities.submissions.controller;

import com.example.children_activities.submissions.dto.CreateSubmissionRequest;
import com.example.children_activities.submissions.dto.SubmissionResponse;
import com.example.children_activities.submissions.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(
            SubmissionService submissionService
    ) {
        this.submissionService = submissionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponse create(
            @Valid @RequestBody CreateSubmissionRequest request
    ) {
        return submissionService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID id
    ) {
        submissionService.delete(id);
    }
}