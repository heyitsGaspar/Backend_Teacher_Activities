package com.example.children_activities.subjects.controller;

import com.example.children_activities.subjects.dto.CreateSubjectRequest;
import com.example.children_activities.subjects.dto.SubjectResponse;
import com.example.children_activities.subjects.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse create(
            @Valid @RequestBody CreateSubjectRequest request
    ) {
        return subjectService.create(request);
    }

    @GetMapping
    public List<SubjectResponse> findAll() {
        return subjectService.findAll();
    }

    @GetMapping("/{id}")
    public SubjectResponse findById(@PathVariable UUID id) {
        return subjectService.findById(id);
    }

    @PutMapping("/{id}")
    public SubjectResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateSubjectRequest request
    ) {
        return subjectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        subjectService.delete(id);
    }
}