package com.example.children_activities.submission.service;

import com.example.children_activities.activities.entity.Activity;
import com.example.children_activities.activities.repository.ActivityRepository;
import com.example.children_activities.children.entity.Child;
import com.example.children_activities.children.repository.ChildRepository;
import com.example.children_activities.exception.ActivityNotFoundException;
import com.example.children_activities.exception.ChildNotFoundException;
import com.example.children_activities.exception.DuplicateSubmissionException;
import com.example.children_activities.exception.SubmissionNotFoundException;
import com.example.children_activities.submissions.dto.CreateSubmissionRequest;
import com.example.children_activities.submissions.dto.SubmissionResponse;
import com.example.children_activities.submissions.entity.Submission;
import com.example.children_activities.submissions.repository.SubmissionRepository;
import com.example.children_activities.submissions.service.SubmissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private ChildRepository childRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private SubmissionService submissionService;


    // =========================================================
    // TEST 1
    // Crear entrega correctamente
    // =========================================================
    @Test
    void shouldCreateSubmissionSuccessfully() {

        // Arrange

        UUID childId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();

        Child child = Child.builder()
                .id(childId)
                .name("Juan")
                .code("ALU-ABCDE")
                .build();

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Matemáticas")
                .activityDate(LocalDate.of(2026, 10, 1))
                .active(true)
                .build();

        CreateSubmissionRequest request =
                new CreateSubmissionRequest(
                        "ALU-ABCDE",
                        activityId
                );

        Submission savedSubmission = Submission.builder()
                .id(submissionId)
                .child(child)
                .activity(activity)
                .build();

        when(childRepository.findByCode("ALU-ABCDE"))
                .thenReturn(Optional.of(child));

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.of(activity));

        when(
                submissionRepository
                        .existsByChildIdAndActivityId(
                                childId,
                                activityId
                        )
        ).thenReturn(false);

        when(submissionRepository.save(any(Submission.class)))
                .thenReturn(savedSubmission);


        // Act

        SubmissionResponse response =
                submissionService.create(request);


        // Assert

        assertNotNull(response);

        assertEquals(submissionId, response.id());

        assertEquals(childId, response.childId());
        assertEquals("Juan", response.childName());
        assertEquals("ALU-ABCDE", response.childCode());

        assertEquals(activityId, response.activityId());
        assertEquals("Matemáticas", response.activityTitle());


        // Verificamos el flujo completo

        verify(childRepository)
                .findByCode("ALU-ABCDE");

        verify(activityRepository)
                .findById(activityId);

        verify(submissionRepository)
                .existsByChildIdAndActivityId(
                        childId,
                        activityId
                );

        verify(submissionRepository)
                .save(any(Submission.class));
    }


    // =========================================================
    // TEST 2
    // Alumno no encontrado
    // =========================================================
    @Test
    void shouldThrowExceptionWhenChildDoesNotExist() {

        // Arrange

        UUID activityId = UUID.randomUUID();

        CreateSubmissionRequest request =
                new CreateSubmissionRequest(
                        "ALU-XXXXX",
                        activityId
                );

        when(childRepository.findByCode("ALU-XXXXX"))
                .thenReturn(Optional.empty());


        // Act + Assert

        assertThrows(
                ChildNotFoundException.class,
                () -> submissionService.create(request)
        );


        // No debemos continuar buscando la actividad

        verify(childRepository)
                .findByCode("ALU-XXXXX");

        verify(activityRepository, never())
                .findById(any(UUID.class));

        verify(submissionRepository, never())
                .save(any(Submission.class));
    }


    // =========================================================
    // TEST 3
    // Actividad no encontrada
    // =========================================================
    @Test
    void shouldThrowExceptionWhenActivityDoesNotExist() {

        // Arrange

        UUID childId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        Child child = Child.builder()
                .id(childId)
                .name("Maria")
                .code("ALU-MARIA")
                .build();

        CreateSubmissionRequest request =
                new CreateSubmissionRequest(
                        "ALU-MARIA",
                        activityId
                );

        when(childRepository.findByCode("ALU-MARIA"))
                .thenReturn(Optional.of(child));

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.empty());


        // Act + Assert

        assertThrows(
                ActivityNotFoundException.class,
                () -> submissionService.create(request)
        );


        verify(childRepository)
                .findByCode("ALU-MARIA");

        verify(activityRepository)
                .findById(activityId);

        // No debemos comprobar duplicados todavía

        verify(
                submissionRepository,
                never()
        ).existsByChildIdAndActivityId(
                any(UUID.class),
                any(UUID.class)
        );

        verify(submissionRepository, never())
                .save(any(Submission.class));
    }


    // =========================================================
    // TEST 4
    // El alumno ya entregó la actividad
    // =========================================================
    @Test
    void shouldThrowExceptionWhenSubmissionAlreadyExists() {

        // Arrange

        UUID childId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        Child child = Child.builder()
                .id(childId)
                .name("Pedro")
                .code("ALU-PEDRO")
                .build();

        Activity activity = Activity.builder()
                .id(activityId)
                .title("Lectura")
                .activityDate(LocalDate.of(2026, 10, 2))
                .active(true)
                .build();

        CreateSubmissionRequest request =
                new CreateSubmissionRequest(
                        "ALU-PEDRO",
                        activityId
                );

        when(childRepository.findByCode("ALU-PEDRO"))
                .thenReturn(Optional.of(child));

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.of(activity));

        when(
                submissionRepository
                        .existsByChildIdAndActivityId(
                                childId,
                                activityId
                        )
        ).thenReturn(true);


        // Act + Assert

        DuplicateSubmissionException exception =
                assertThrows(
                        DuplicateSubmissionException.class,
                        () -> submissionService.create(request)
                );


        assertEquals(
                "El alumno ya entregó esta actividad",
                exception.getMessage()
        );


        // No debe guardar una segunda entrega

        verify(submissionRepository, never())
                .save(any(Submission.class));
    }


    // =========================================================
    // TEST 5
    // Eliminar entrega correctamente
    // =========================================================
    @Test
    void shouldDeleteSubmissionSuccessfully() {

        // Arrange

        UUID submissionId = UUID.randomUUID();

        Child child = Child.builder()
                .id(UUID.randomUUID())
                .name("Carlos")
                .code("ALU-CARLO")
                .build();

        Activity activity = Activity.builder()
                .id(UUID.randomUUID())
                .title("Historia")
                .activityDate(LocalDate.of(2026, 10, 3))
                .active(true)
                .build();

        Submission submission = Submission.builder()
                .id(submissionId)
                .child(child)
                .activity(activity)
                .build();

        when(submissionRepository.findById(submissionId))
                .thenReturn(Optional.of(submission));


        // Act

        submissionService.delete(submissionId);


        // Assert

        verify(submissionRepository)
                .findById(submissionId);

        verify(submissionRepository)
                .delete(submission);
    }


    // =========================================================
    // TEST 6
    // Intentar eliminar entrega que no existe
    // =========================================================
    @Test
    void shouldThrowExceptionWhenSubmissionDoesNotExist() {

        // Arrange

        UUID submissionId = UUID.randomUUID();

        when(submissionRepository.findById(submissionId))
                .thenReturn(Optional.empty());


        // Act + Assert

        assertThrows(
                SubmissionNotFoundException.class,
                () -> submissionService.delete(submissionId)
        );


        verify(submissionRepository)
                .findById(submissionId);

        verify(submissionRepository, never())
                .delete(any(Submission.class));
    }
}