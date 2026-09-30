package com.example.children_activities.activity.service;

import com.example.children_activities.activities.dto.ActivityResponse;
import com.example.children_activities.activities.dto.CreateActivityRequest;
import com.example.children_activities.activities.entity.Activity;
import com.example.children_activities.activities.repository.ActivityRepository;
import com.example.children_activities.activities.service.ActivityService;
import com.example.children_activities.exception.ActivityNotFoundException;
import com.example.children_activities.exception.SubjectNotFoundException;
import com.example.children_activities.subjects.entity.Subject;
import com.example.children_activities.subjects.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private ActivityService activityService;


    // =========================================================
    // TEST 1
    // Crear actividad correctamente
    // =========================================================
    @Test
    void shouldCreateActivitySuccessfully() {

        // Arrange

        UUID subjectId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(subjectId)
                .name("Matemáticas")
                .build();

        CreateActivityRequest request =
                new CreateActivityRequest(
                        subjectId,
                        "Fracciones",
                        LocalDate.of(2026, 10, 1)
                );

        Activity savedActivity = Activity.builder()
                .id(activityId)
                .subject(subject)
                .title("Fracciones")
                .activityDate(LocalDate.of(2026, 10, 1))
                .active(false)
                .build();

        when(subjectRepository.findById(subjectId))
                .thenReturn(Optional.of(subject));

        when(activityRepository.save(any(Activity.class)))
                .thenReturn(savedActivity);


        // Act

        ActivityResponse response =
                activityService.create(request);


        // Assert

        assertNotNull(response);

        assertEquals(activityId, response.id());
        assertEquals(subjectId, response.subjectId());
        assertEquals("Matemáticas", response.subjectName());
        assertEquals("Fracciones", response.title());
        assertEquals(
                LocalDate.of(2026, 10, 1),
                response.activityDate()
        );
        assertFalse(response.active());

        verify(subjectRepository)
                .findById(subjectId);

        verify(activityRepository)
                .save(any(Activity.class));
    }


    // =========================================================
    // TEST 2
    // Crear actividad cuando la asignatura no existe
    // =========================================================
    @Test
    void shouldThrowExceptionWhenSubjectDoesNotExistOnCreate() {

        // Arrange

        UUID subjectId = UUID.randomUUID();

        CreateActivityRequest request =
                new CreateActivityRequest(
                        subjectId,
                        "Fracciones",
                        LocalDate.of(2026, 10, 1)
                );

        when(subjectRepository.findById(subjectId))
                .thenReturn(Optional.empty());


        // Act + Assert

        assertThrows(
                SubjectNotFoundException.class,
                () -> activityService.create(request)
        );

        verify(subjectRepository)
                .findById(subjectId);

        verify(activityRepository, never())
                .save(any(Activity.class));
    }


    // =========================================================
    // TEST 3
    // Obtener todas las actividades
    // =========================================================
    @Test
    void shouldFindAllActivities() {

        // Arrange

        Subject subject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Español")
                .build();

        Activity activity1 = Activity.builder()
                .id(UUID.randomUUID())
                .subject(subject)
                .title("Lectura")
                .activityDate(LocalDate.of(2026, 10, 1))
                .active(false)
                .build();

        Activity activity2 = Activity.builder()
                .id(UUID.randomUUID())
                .subject(subject)
                .title("Gramática")
                .activityDate(LocalDate.of(2026, 10, 2))
                .active(false)
                .build();

        when(activityRepository.findAll())
                .thenReturn(List.of(activity1, activity2));


        // Act

        List<ActivityResponse> response =
                activityService.findAll();


        // Assert

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("Lectura", response.get(0).title());
        assertEquals("Gramática", response.get(1).title());

        verify(activityRepository)
                .findAll();
    }


    // =========================================================
    // TEST 4
    // Obtener actividad por ID correctamente
    // =========================================================
    @Test
    void shouldFindActivityByIdSuccessfully() {

        // Arrange

        UUID activityId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Ciencias")
                .build();

        Activity activity = Activity.builder()
                .id(activityId)
                .subject(subject)
                .title("El sistema solar")
                .activityDate(LocalDate.of(2026, 10, 5))
                .active(false)
                .build();

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.of(activity));


        // Act

        ActivityResponse response =
                activityService.findById(activityId);


        // Assert

        assertNotNull(response);

        assertEquals(activityId, response.id());
        assertEquals("El sistema solar", response.title());
        assertEquals("Ciencias", response.subjectName());

        verify(activityRepository)
                .findById(activityId);
    }


    // =========================================================
    // TEST 5
    // Obtener actividad que no existe
    // =========================================================
    @Test
    void shouldThrowExceptionWhenActivityDoesNotExist() {

        // Arrange

        UUID activityId = UUID.randomUUID();

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.empty());


        // Act + Assert

        assertThrows(
                ActivityNotFoundException.class,
                () -> activityService.findById(activityId)
        );

        verify(activityRepository)
                .findById(activityId);
    }


    // =========================================================
    // TEST 6
    // Actualizar actividad correctamente
    // =========================================================
    @Test
    void shouldUpdateActivitySuccessfully() {

        // Arrange

        UUID activityId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        Subject oldSubject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Matemáticas")
                .build();

        Subject newSubject = Subject.builder()
                .id(subjectId)
                .name("Ciencias")
                .build();

        Activity activity = Activity.builder()
                .id(activityId)
                .subject(oldSubject)
                .title("Actividad anterior")
                .activityDate(LocalDate.of(2026, 10, 1))
                .active(false)
                .build();

        CreateActivityRequest request =
                new CreateActivityRequest(
                        subjectId,
                        "Nueva actividad",
                        LocalDate.of(2026, 10, 10)
                );

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.of(activity));

        when(subjectRepository.findById(subjectId))
                .thenReturn(Optional.of(newSubject));

        when(activityRepository.save(any(Activity.class)))
                .thenReturn(activity);


        // Act

        ActivityResponse response =
                activityService.update(activityId, request);


        // Assert

        assertNotNull(response);

        assertEquals(activityId, response.id());
        assertEquals(subjectId, response.subjectId());
        assertEquals("Ciencias", response.subjectName());
        assertEquals("Nueva actividad", response.title());
        assertEquals(
                LocalDate.of(2026, 10, 10),
                response.activityDate()
        );

        verify(activityRepository)
                .findById(activityId);

        verify(subjectRepository)
                .findById(subjectId);

        verify(activityRepository)
                .save(activity);
    }


    // =========================================================
    // TEST 7
    // Eliminar actividad correctamente
    // =========================================================
    @Test
    void shouldDeleteActivitySuccessfully() {

        // Arrange

        UUID activityId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Historia")
                .build();

        Activity activity = Activity.builder()
                .id(activityId)
                .subject(subject)
                .title("La Independencia")
                .activityDate(LocalDate.of(2026, 10, 15))
                .active(false)
                .build();

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.of(activity));


        // Act

        activityService.delete(activityId);


        // Assert

        verify(activityRepository)
                .findById(activityId);

        verify(activityRepository)
                .delete(activity);
    }


    // =========================================================
    // TEST 8
    // Activar actividad cuando no hay otra activa
    // =========================================================
    @Test
    void shouldActivateActivityWhenThereIsNoOtherActiveActivity() {

        // Arrange

        UUID activityId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Matemáticas")
                .build();

        Activity activity = Activity.builder()
                .id(activityId)
                .subject(subject)
                .title("Multiplicaciones")
                .activityDate(LocalDate.of(2026, 10, 20))
                .active(false)
                .build();

        when(activityRepository.findById(activityId))
                .thenReturn(Optional.of(activity));

        when(activityRepository.findByActiveTrue())
                .thenReturn(Optional.empty());

        when(activityRepository.save(any(Activity.class)))
                .thenReturn(activity);


        // Act

        ActivityResponse response =
                activityService.activate(activityId);


        // Assert

        assertNotNull(response);
        assertTrue(response.active());

        verify(activityRepository)
                .findById(activityId);

        verify(activityRepository)
                .findByActiveTrue();

        verify(activityRepository)
                .save(activity);
    }


    // =========================================================
    // TEST 9
    // Activar actividad cuando ya existe otra activa
    // =========================================================
    @Test
    void shouldDeactivatePreviousActivityWhenActivatingAnother() {

        // Arrange

        UUID oldActivityId = UUID.randomUUID();
        UUID newActivityId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Español")
                .build();

        Activity oldActivity = Activity.builder()
                .id(oldActivityId)
                .subject(subject)
                .title("Actividad anterior")
                .activityDate(LocalDate.of(2026, 10, 20))
                .active(true)
                .build();

        Activity newActivity = Activity.builder()
                .id(newActivityId)
                .subject(subject)
                .title("Actividad nueva")
                .activityDate(LocalDate.of(2026, 10, 21))
                .active(false)
                .build();

        when(activityRepository.findById(newActivityId))
                .thenReturn(Optional.of(newActivity));

        when(activityRepository.findByActiveTrue())
                .thenReturn(Optional.of(oldActivity));

        when(activityRepository.save(any(Activity.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));


        // Act

        ActivityResponse response =
                activityService.activate(newActivityId);


        // Assert

        assertNotNull(response);

        assertTrue(response.active());

        // La actividad anterior debe quedar desactivada
        assertFalse(oldActivity.isActive());

        // La nueva debe quedar activa
        assertTrue(newActivity.isActive());

        // Se guarda la anterior y después la nueva
        verify(activityRepository)
                .save(oldActivity);

        verify(activityRepository)
                .save(newActivity);
    }


    // =========================================================
    // TEST 10
    // Obtener actividad activa correctamente
    // =========================================================
    @Test
    void shouldFindActiveActivitySuccessfully() {

        // Arrange

        UUID activityId = UUID.randomUUID();

        Subject subject = Subject.builder()
                .id(UUID.randomUUID())
                .name("Ciencias")
                .build();

        Activity activity = Activity.builder()
                .id(activityId)
                .subject(subject)
                .title("Los planetas")
                .activityDate(LocalDate.of(2026, 10, 25))
                .active(true)
                .build();

        when(activityRepository.findByActiveTrue())
                .thenReturn(Optional.of(activity));


        // Act

        ActivityResponse response =
                activityService.findActive();


        // Assert

        assertNotNull(response);

        assertEquals(activityId, response.id());
        assertEquals("Los planetas", response.title());
        assertTrue(response.active());

        verify(activityRepository)
                .findByActiveTrue();
    }


    // =========================================================
    // TEST 11
    // Buscar actividad activa cuando no existe
    // =========================================================
    @Test
    void shouldThrowExceptionWhenThereIsNoActiveActivity() {

        // Arrange

        when(activityRepository.findByActiveTrue())
                .thenReturn(Optional.empty());


        // Act + Assert

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> activityService.findActive()
        );

        assertEquals(
                "No hay ninguna actividad activa",
                exception.getMessage()
        );

        verify(activityRepository)
                .findByActiveTrue();
    }
}