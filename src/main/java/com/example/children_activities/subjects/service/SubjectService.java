package com.example.children_activities.subjects.service;

import com.example.children_activities.auth.security.CurrentUserService;
import com.example.children_activities.exception.DuplicateSubjectException;
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
    /**
     * Crea una nueva asignatura y la asigna
     * al maestro actualmente autenticado.
     *
     * No permite crear dos asignaturas con
     * el mismo nombre para el mismo maestro.
     */
    @Transactional
    public SubjectResponse create(CreateSubjectRequest request) {

        UUID teacherId = currentUserService.getCurrentUserId();

        String name = request.name().trim();

        /*
         * Verificamos si el maestro ya tiene
         * una asignatura con ese nombre.
         *
         * IgnoreCase permite considerar como iguales:
         *
         * "Matemáticas"
         * "matemáticas"
         * "MATEMÁTICAS"
         */
        if (subjectRepository.existsByNameIgnoreCaseAndTeacherId(
                name,
                teacherId
        )) {

            throw new DuplicateSubjectException(
                    "Ya existe una asignatura con ese nombre"
            );
        }

        Subject subject = Subject.builder()
                .name(name)
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
     /**
     * Actualiza una asignatura únicamente si
     * pertenece al maestro autenticado.
     *
     * No permite cambiar el nombre por uno
     * que ya esté utilizado por otra asignatura
     * del mismo maestro.
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

        String name = request.name().trim();

        /*
         * Solo comprobamos duplicados si el nuevo nombre
         * es diferente al nombre actual.
         */
        if (!subject.getName().equalsIgnoreCase(name)
                && subjectRepository.existsByNameIgnoreCaseAndTeacherId(
                name,
                teacherId
        )) {

            throw new DuplicateSubjectException(
                    "Ya existe una asignatura con ese nombre"
            );
        }

        subject.setName(name);

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