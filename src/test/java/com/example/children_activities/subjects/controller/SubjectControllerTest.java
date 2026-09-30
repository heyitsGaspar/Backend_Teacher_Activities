package com.example.children_activities.subjects.controller;

import com.example.children_activities.auth.security.CustomUserDetailsService;
import com.example.children_activities.auth.security.JwtService;
import com.example.children_activities.subjects.dto.CreateSubjectRequest;
import com.example.children_activities.subjects.dto.SubjectResponse;
import com.example.children_activities.subjects.service.SubjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubjectController.class)
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SubjectService subjectService;

    // Security dependencies required by @WebMvcTest
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldCreateSubjectSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 30, 10, 30);

        CreateSubjectRequest request =
                new CreateSubjectRequest("Matemáticas");

        SubjectResponse response =
                new SubjectResponse(
                        id,
                        "Matemáticas",
                        createdAt
                );

        when(subjectService.create(any(CreateSubjectRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/subjects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Matemáticas"));

        verify(subjectService)
                .create(any(CreateSubjectRequest.class));
    }

    @Test
    void shouldFindAllSubjectsSuccessfully() throws Exception {

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 30, 10, 30);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<SubjectResponse> responses = List.of(
                new SubjectResponse(
                        id1,
                        "Matemáticas",
                        createdAt

                ),
                new SubjectResponse(
                        id2,
                        "Español",
                        createdAt
                )
        );

        when(subjectService.findAll())
                .thenReturn(responses);

        mockMvc.perform(
                        get("/api/subjects")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].name").value("Matemáticas"))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].name").value("Español"));

        verify(subjectService).findAll();
    }

    @Test
    void shouldFindSubjectByIdSuccessfully() throws Exception {

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 30, 10, 30);

        UUID id = UUID.randomUUID();

        SubjectResponse response =
                new SubjectResponse(
                        id,
                        "Matemáticas",
                        createdAt
                );

        when(subjectService.findById(id))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/subjects/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Matemáticas"));

        verify(subjectService).findById(id);
    }

    @Test
    void shouldUpdateSubjectSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        CreateSubjectRequest request =
                new CreateSubjectRequest("Matemáticas Avanzadas");

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 30, 10, 30);

        SubjectResponse response =
                new SubjectResponse(
                        id,
                        "Matemáticas Avanzadas",
                        createdAt
                );

        when(subjectService.update(
                any(UUID.class),
                any(CreateSubjectRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/subjects/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Matemáticas Avanzadas"));

        verify(subjectService).update(
                any(UUID.class),
                any(CreateSubjectRequest.class)
        );
    }

    @Test
    void shouldDeleteSubjectSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();


        doNothing()
                .when(subjectService)
                .delete(id);

        mockMvc.perform(
                        delete("/api/subjects/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(subjectService).delete(id);
    }

    @Test
    void shouldRejectCreateSubjectWithBlankName() throws Exception {

        CreateSubjectRequest request =
                new CreateSubjectRequest("");

        mockMvc.perform(
                        post("/api/subjects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}