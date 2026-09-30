package com.example.children_activities.subjects.service;

import com.example.children_activities.exception.SubjectNotFoundException;
import com.example.children_activities.subjects.dto.CreateSubjectRequest;
import com.example.children_activities.subjects.dto.SubjectResponse;
import com.example.children_activities.subjects.entity.Subject;
import com.example.children_activities.subjects.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;


    // =========================================================
    // TEST 1
    // Crear asignatura correctamente
    // =========================================================
    @Test
    void shouldCreateSubjectSuccessfully() {

        // Arrange
        CreateSubjectRequest request =
                new CreateSubjectRequest("Matemáticas");

        UUID subjectId = UUID.randomUUID();

        Subject savedSubject = Subject.builder()
                .id(subjectId)
                .name("Matemáticas")
                .build();

        when(subjectRepository.save(any(Subject.class)))
                .thenReturn(savedSubject);


        // Act
        SubjectResponse response =
                subjectService.create(request);


        // Assert
        assertNotNull(response);

        assertEquals(subjectId, response.id());
        assertEquals("Matemáticas", response.name());

        verify(subjectRepository)
                .save(any(Subject.class));
    }


    // =========================================================
    // TEST 2
    // Obtener todas las asignaturas
    // =========================================================
    @Test
    void shouldFindAllSubjects() {

        // Arrange

        Subject subject1 = Subject.builder()
                .id(UUID.randomUUID())
                .name("Matemáticas")
                .build();

        Subject subject2 = Subject.builder()
                .id(UUID.randomUUID())
                .name("Español")
                .build();

        when(subjectRepository.findAll())
                .thenReturn(List.of(subject1, subject2));


        // Act

        List<SubjectResponse> response =
                subjectService.findAll();


        // Assert

        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals("Matemáticas", response.get(0).name());
        assertEquals("Español", response.get(1).name());

        verify(subjectRepository)
                .findAll();
    }


    // =========================================================
    // TEST 3
    // Obtener asignatura por ID cuando existe
    // =========================================================
    @Test
    void shouldFindSubjectByIdSuccessfully() {

        // Arrange

        UUID subjectId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(subjectId)
                .name("Historia")
                .build();

        when(subjectRepository.findById(subjectId))
                .thenReturn(Optional.of(subject));


        // Act

        SubjectResponse response =
                subjectService.findById(subjectId);


        // Assert

        assertNotNull(response);

        assertEquals(subjectId, response.id());
        assertEquals("Historia", response.name());

        verify(subjectRepository)
                .findById(subjectId);
    }


    // =========================================================
    // TEST 4
    // Buscar asignatura que NO existe
    // =========================================================
    @Test
    void shouldThrowExceptionWhenSubjectDoesNotExist() {

        // Arrange

        UUID subjectId = UUID.randomUUID();

        when(subjectRepository.findById(subjectId))
                .thenReturn(Optional.empty());


        // Act + Assert

        RuntimeException exception = assertThrows(
                SubjectNotFoundException.class,
                () -> subjectService.findById(subjectId)
        );

        assertEquals(
                "Asignatura no encontrada",
                exception.getMessage()
        );

        verify(subjectRepository)
                .findById(subjectId);
    }


    // =========================================================
    // TEST 5
    // Actualizar asignatura correctamente
    // =========================================================
    @Test
    void shouldUpdateSubjectSuccessfully() {

        // Arrange

        UUID subjectId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(subjectId)
                .name("Matemáticas")
                .build();

        CreateSubjectRequest request =
                new CreateSubjectRequest("Matemáticas Avanzadas");

        when(subjectRepository.findById(subjectId))
                .thenReturn(Optional.of(subject));

        when(subjectRepository.save(any(Subject.class)))
                .thenReturn(subject);


        // Act

        SubjectResponse response =
                subjectService.update(subjectId, request);


        // Assert

        assertNotNull(response);

        assertEquals(subjectId, response.id());
        assertEquals(
                "Matemáticas Avanzadas",
                response.name()
        );

        verify(subjectRepository)
                .findById(subjectId);

        verify(subjectRepository)
                .save(subject);
    }


    // =========================================================
    // TEST 6
    // Eliminar asignatura correctamente
    // =========================================================
    @Test
    void shouldDeleteSubjectSuccessfully() {

        // Arrange

        UUID subjectId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(subjectId)
                .name("Historia")
                .build();

        when(subjectRepository.findById(subjectId))
                .thenReturn(Optional.of(subject));


        // Act

        subjectService.delete(subjectId);


        // Assert

        verify(subjectRepository)
                .findById(subjectId);

        verify(subjectRepository)
                .delete(subject);
    }
}