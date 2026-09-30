package com.example.children_activities.children.service;

import com.example.children_activities.children.dto.ChildResponse;
import com.example.children_activities.children.dto.CreateChildRequest;
import com.example.children_activities.children.entity.Child;
import com.example.children_activities.children.repository.ChildRepository;
import com.example.children_activities.exception.ChildNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChildServiceTest {

    @Mock
    private ChildRepository childRepository;

    @InjectMocks
    private ChildService childService;

    @Test
    void shouldCreateChildSuccessfully() {

        // Arrange
        CreateChildRequest request =
                new CreateChildRequest("Juan");

        Child savedChild = Child.builder()
                .id(UUID.randomUUID())
                .name("Juan")
                .code("ALU-ABCDE")
                .build();

        when(childRepository.existsByCode(any(String.class)))
                .thenReturn(false);

        when(childRepository.save(any(Child.class)))
                .thenReturn(savedChild);

        // Act
        ChildResponse response =
                childService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("Juan", response.name());
        assertEquals("ALU-ABCDE", response.code());

        verify(childRepository)
                .save(any(Child.class));
    }

    // =========================================================
    // TEST 2
    // Obtener todos los alumnos
    // =========================================================
    @Test
    void shouldFindAllChildren() {

        // Arrange

        Child child1 = Child.builder()
                .id(UUID.randomUUID())
                .name("Juan")
                .code("ALU-AAAAA")
                .build();

        Child child2 = Child.builder()
                .id(UUID.randomUUID())
                .name("Maria")
                .code("ALU-BBBBB")
                .build();

        when(childRepository.findAll())
                .thenReturn(List.of(child1, child2));


        // Act

        List<ChildResponse> response =
                childService.findAll();


        // Assert

        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals("Juan", response.get(0).name());
        assertEquals("ALU-AAAAA", response.get(0).code());

        assertEquals("Maria", response.get(1).name());
        assertEquals("ALU-BBBBB", response.get(1).code());


        // Verificamos que el repository realmente fue llamado

        verify(childRepository)
                .findAll();
    }


    // =========================================================
    // TEST 3
    // Obtener alumno por ID cuando existe
    // =========================================================
    @Test
    void shouldFindChildByIdSuccessfully() {

        // Arrange

        UUID childId = UUID.randomUUID();

        Child child = Child.builder()
                .id(childId)
                .name("Carlos")
                .code("ALU-CCCCC")
                .build();

        when(childRepository.findById(childId))
                .thenReturn(java.util.Optional.of(child));


        // Act

        ChildResponse response =
                childService.findById(childId);


        // Assert

        assertNotNull(response);

        assertEquals("Carlos", response.name());
        assertEquals("ALU-CCCCC", response.code());


        // Verificamos que se buscó exactamente ese ID

        verify(childRepository)
                .findById(childId);
    }


    // =========================================================
    // TEST 4
    // Obtener alumno por ID cuando NO existe
    // =========================================================
    @Test
    void shouldThrowExceptionWhenChildDoesNotExist() {

        // Arrange

        UUID childId = UUID.randomUUID();

        when(childRepository.findById(childId))
                .thenReturn(java.util.Optional.empty());


        // Act + Assert

        assertThrows(
                ChildNotFoundException.class,
                () -> childService.findById(childId)
        );


        // Verificamos que se intentó buscar el alumno

        verify(childRepository)
                .findById(childId);
    }
}