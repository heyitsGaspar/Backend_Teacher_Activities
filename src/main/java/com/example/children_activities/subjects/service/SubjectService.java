package com.example.children_activities.subjects.service;

import com.example.children_activities.auth.security.CurrentUserService;
import com.example.children_activities.exception.SubjectNotFoundException;
import com.example.children_activities.subjects.dto.CreateSubjectRequest;
import com.example.children_activities.subjects.dto.SubjectResponse;
import com.example.children_activities.subjects.entity.Subject;
import com.example.children_activities.subjects.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final CurrentUserService currentUserService;

    public SubjectService(
            SubjectRepository subjectRepository,
            CurrentUserService currentUserService
    ) {
        this.subjectRepository = subjectRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Crea una nueva asignatura y la asigna
     * al maestro actualmente autenticado.
     */
    @Transactional
    public SubjectResponse create(CreateSubjectRequest request) {

        Subject subject = Subject.builder()
                .name(request.name())
                .teacher(currentUserService.getCurrentUser())
                .build();

        Subject savedSubject = subjectRepository.save(subject);

        return SubjectResponse.fromEntity(savedSubject);
    }

    /**
     * Obtiene únicamente las asignaturas
     * del maestro autenticado.
     *
     * Utiliza paginación.
     */
    @Transactional(readOnly = true)
    public Page<SubjectResponse> findAll(Pageable pageable) {

        UUID teacherId = currentUserService.getCurrentUserId();

        return subjectRepository
                .findByTeacherId(teacherId, pageable)
                .map(SubjectResponse::fromEntity);
    }

    /**
     * Busca una asignatura por ID,
     * verificando que pertenezca al maestro autenticado.
     */
    @Transactional(readOnly = true)
    public SubjectResponse findById(UUID id) {

        UUID teacherId = currentUserService.getCurrentUserId();

        Subject subject = subjectRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() ->
                        new SubjectNotFoundException(
                                "Asignatura no encontrada"
                        )
                );

        return SubjectResponse.fromEntity(subject);
    }

    /**
     * Actualiza una asignatura únicamente si
     * pertenece al maestro autenticado.
     */
    @Transactional
    public SubjectResponse update(
            UUID id,
            CreateSubjectRequest request
    ) {

        UUID teacherId = currentUserService.getCurrentUserId();

        Subject subject = subjectRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() ->
                        new SubjectNotFoundException(
                                "Asignatura no encontrada"
                        )
                );

        subject.setName(request.name());

        Subject updatedSubject = subjectRepository.save(subject);

        return SubjectResponse.fromEntity(updatedSubject);
    }

    /**
     * Elimina una asignatura únicamente si
     * pertenece al maestro autenticado.
     */
    @Transactional
    public void delete(UUID id) {

        UUID teacherId = currentUserService.getCurrentUserId();

        Subject subject = subjectRepository
                .findByIdAndTeacherId(id, teacherId)
                .orElseThrow(() ->
                        new SubjectNotFoundException(
                                "Asignatura no encontrada"
                        )
                );

        subjectRepository.delete(subject);
    }
}