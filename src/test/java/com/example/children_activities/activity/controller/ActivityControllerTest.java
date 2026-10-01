package com.example.children_activities.activity.controller;

import com.example.children_activities.activities.controller.ActivityController;
import com.example.children_activities.activities.dto.ActivityResponse;
import com.example.children_activities.activities.dto.CreateActivityRequest;
import com.example.children_activities.activities.service.ActivityService;
import com.example.children_activities.auth.security.CustomUserDetailsService;
import com.example.children_activities.auth.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@WebMvcTest(ActivityController.class)
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ActivityService activityService;

    // Security dependencies required by @WebMvcTest
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper()
                    .registerModule(new JavaTimeModule());
        }
    }

    @Test
    void shouldCreateActivitySuccessfully() throws Exception {

        UUID activityId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CreateActivityRequest request =
                new CreateActivityRequest(
                        subjectId,
                        "Examen de Matemáticas",
                        LocalDate.of(2026, 10, 5)
                );

        ActivityResponse response =
                new ActivityResponse(
                        activityId,
                        subjectId,
                        "Matemáticas",
                        "Examen de Matemáticas",
                        LocalDate.of(2026, 10, 5),
                        false,
                        LocalDateTime.of(2026, 9, 30, 10, 30)
                );

        when(activityService.create(
                any(CreateActivityRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(activityId.toString()))
                .andExpect(jsonPath("$.subjectId")
                        .value(subjectId.toString()))
                .andExpect(jsonPath("$.subjectName")
                        .value("Matemáticas"))
                .andExpect(jsonPath("$.title")
                        .value("Examen de Matemáticas"))
                .andExpect(jsonPath("$.active")
                        .value(false));

        verify(activityService)
                .create(any(CreateActivityRequest.class));
    }

    @Test
    void shouldFindAllActivitiesSuccessfully() throws Exception {

        UUID activityId1 = UUID.randomUUID();
        UUID activityId2 = UUID.randomUUID();

        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();

        List<ActivityResponse> responses = List.of(

                new ActivityResponse(
                        activityId1,
                        subjectId1,
                        "Matemáticas",
                        "Examen parcial",
                        LocalDate.of(2026, 10, 5),
                        false,
                        LocalDateTime.of(2026, 9, 30, 10, 30)
                ),

                new ActivityResponse(
                        activityId2,
                        subjectId2,
                        "Español",
                        "Lectura",
                        LocalDate.of(2026, 10, 6),
                        true,
                        LocalDateTime.of(2026, 9, 30, 10, 30)
                )
        );

        when(activityService.findAll())
                .thenReturn(responses);

        mockMvc.perform(
                        get("/api/activities")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))

                .andExpect(jsonPath("$[0].id")
                        .value(activityId1.toString()))
                .andExpect(jsonPath("$[0].subjectId")
                        .value(subjectId1.toString()))
                .andExpect(jsonPath("$[0].title")
                        .value("Examen parcial"))
                .andExpect(jsonPath("$[0].active")
                        .value(false))

                .andExpect(jsonPath("$[1].id")
                        .value(activityId2.toString()))
                .andExpect(jsonPath("$[1].subjectId")
                        .value(subjectId2.toString()))
                .andExpect(jsonPath("$[1].title")
                        .value("Lectura"))
                .andExpect(jsonPath("$[1].active")
                        .value(true));

        verify(activityService).findAll();
    }

    @Test
    void shouldFindActivityByIdSuccessfully() throws Exception {

        UUID activityId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        ActivityResponse response =
                new ActivityResponse(
                        activityId,
                        subjectId,
                        "Matemáticas",
                        "Examen parcial",
                        LocalDate.of(2026, 10, 5),
                        false,
                        LocalDateTime.of(2026, 9, 30, 10, 30)
                );

        when(activityService.findById(activityId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/activities/{id}", activityId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(activityId.toString()))
                .andExpect(jsonPath("$.subjectId")
                        .value(subjectId.toString()))
                .andExpect(jsonPath("$.subjectName")
                        .value("Matemáticas"))
                .andExpect(jsonPath("$.title")
                        .value("Examen parcial"))
                .andExpect(jsonPath("$.active")
                        .value(false));

        verify(activityService)
                .findById(activityId);
    }

    @Test
    void shouldUpdateActivitySuccessfully() throws Exception {

        UUID activityId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        CreateActivityRequest request =
                new CreateActivityRequest(
                        subjectId,
                        "Examen final",
                        LocalDate.of(2026, 11, 10)
                );

        ActivityResponse response =
                new ActivityResponse(
                        activityId,
                        subjectId,
                        "Matemáticas",
                        "Examen final",
                        LocalDate.of(2026, 11, 10),
                        false,
                        LocalDateTime.of(2026, 9, 30, 10, 30)
                );

        when(activityService.update(
                any(UUID.class),
                any(CreateActivityRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/activities/{id}", activityId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(activityId.toString()))
                .andExpect(jsonPath("$.subjectId")
                        .value(subjectId.toString()))
                .andExpect(jsonPath("$.title")
                        .value("Examen final"))
                .andExpect(jsonPath("$.active")
                        .value(false));

        verify(activityService).update(
                any(UUID.class),
                any(CreateActivityRequest.class)
        );
    }

    @Test
    void shouldDeleteActivitySuccessfully() throws Exception {

        UUID activityId = UUID.randomUUID();

        doNothing()
                .when(activityService)
                .delete(activityId);

        mockMvc.perform(
                        delete("/api/activities/{id}", activityId)
                )
                .andExpect(status().isNoContent());

        verify(activityService)
                .delete(activityId);
    }

    @Test
    void shouldActivateActivitySuccessfully() throws Exception {

        UUID activityId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        ActivityResponse response =
                new ActivityResponse(
                        activityId,
                        subjectId,
                        "Matemáticas",
                        "Examen parcial",
                        LocalDate.of(2026, 10, 5),
                        true,
                        LocalDateTime.of(2026, 9, 30, 10, 30)
                );

        when(activityService.activate(activityId))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/activities/{id}/activate", activityId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(activityId.toString()))
                .andExpect(jsonPath("$.active")
                        .value(true));

        verify(activityService)
                .activate(activityId);
    }

    @Test
    void shouldFindActiveActivitySuccessfully() throws Exception {

        UUID activityId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        ActivityResponse response =
                new ActivityResponse(
                        activityId,
                        subjectId,
                        "Matemáticas",
                        "Examen parcial",
                        LocalDate.of(2026, 10, 5),
                        true,
                        LocalDateTime.of(2026, 9, 30, 10, 30)
                );

        when(activityService.findActive())
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/activities/active/current")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(activityId.toString()))
                .andExpect(jsonPath("$.subjectId")
                        .value(subjectId.toString()))
                .andExpect(jsonPath("$.title")
                        .value("Examen parcial"))
                .andExpect(jsonPath("$.active")
                        .value(true));

        verify(activityService)
                .findActive();
    }

    @Test
    void shouldRejectCreateActivityWithInvalidData() throws Exception {

        CreateActivityRequest request =
                new CreateActivityRequest(
                        null,
                        "",
                        null
                );

        mockMvc.perform(
                        post("/api/activities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}