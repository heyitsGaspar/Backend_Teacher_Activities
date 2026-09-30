package com.example.children_activities.subjects.service;

import com.example.children_activities.exception.SubjectNotFoundException;
import com.example.children_activities.subjects.dto.CreateSubjectRequest;
import com.example.children_activities.subjects.dto.SubjectResponse;
import com.example.children_activities.subjects.entity.Subject;
import com.example.children_activities.subjects.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public SubjectResponse create(CreateSubjectRequest request) {

        Subject subject = Subject.builder()
                .name(request.name())
                .build();

        Subject savedSubject = subjectRepository.save(subject);

        return SubjectResponse.fromEntity(savedSubject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findAll() {

        return subjectRepository.findAll()
                .stream()
                .map(SubjectResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse findById(UUID id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Asignatura no encontrada")
                );

        return SubjectResponse.fromEntity(subject);
    }
    @Transactional
    public SubjectResponse update(UUID id, CreateSubjectRequest request) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Asignatura no encontrada")
                );

        subject.setName(request.name());

        Subject updatedSubject = subjectRepository.save(subject);

        return SubjectResponse.fromEntity(updatedSubject);
    }

    @Transactional
    public void delete(UUID id) {

        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() ->
                        new SubjectNotFoundException("Asignatura no encontrada")
                );

        subjectRepository.delete(subject);
    }
}

