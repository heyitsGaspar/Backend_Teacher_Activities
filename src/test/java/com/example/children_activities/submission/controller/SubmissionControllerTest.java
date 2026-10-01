package com.example.children_activities.submission.controller;

import com.example.children_activities.auth.security.CustomUserDetailsService;
import com.example.children_activities.auth.security.JwtService;
import com.example.children_activities.submissions.controller.SubmissionController;
import com.example.children_activities.submissions.dto.CreateSubmissionRequest;
import com.example.children_activities.submissions.dto.SubmissionResponse;
import com.example.children_activities.submissions.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubmissionController.class)
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Service que estamos probando indirectamente desde el controller
    @MockitoBean
    private SubmissionService submissionService;

    // Security dependencies required by @WebMvcTest
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    // ============================================================
    // POST /api/submissions
    // ============================================================

    @Test
    void shouldCreateSubmissionSuccessfully() throws Exception {

        // Arrange
        UUID submissionId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        CreateSubmissionRequest request = new CreateSubmissionRequest(
                "ALU-001",
                activityId
        );

        SubmissionResponse response = new SubmissionResponse(
                submissionId,
                childId,
                "Juan Pérez",
                "ALU-001",
                activityId,
                "Actividad de Matemáticas",
                LocalDateTime.now()
        );

        when(submissionService.create(any(CreateSubmissionRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(
                        post("/api/submissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(submissionId.toString()))
                .andExpect(jsonPath("$.childId").value(childId.toString()))
                .andExpect(jsonPath("$.childName").value("Juan Pérez"))
                .andExpect(jsonPath("$.childCode").value("ALU-001"))
                .andExpect(jsonPath("$.activityId").value(activityId.toString()))
                .andExpect(jsonPath("$.activityTitle").value("Actividad de Matemáticas"));

        verify(submissionService).create(any(CreateSubmissionRequest.class));
    }


    // ============================================================
    // DELETE /api/submissions/{id}
    // ============================================================

    @Test
    void shouldDeleteSubmissionSuccessfully() throws Exception {

        // Arrange
        UUID submissionId = UUID.randomUUID();

        doNothing().when(submissionService).delete(submissionId);

        // Act & Assert
        mockMvc.perform(
                        delete("/api/submissions/{id}", submissionId)
                )
                .andExpect(status().isNoContent());

        verify(submissionService).delete(submissionId);
    }


    // ============================================================
    // POST /api/submissions
    // Validation: code vacío
    // ============================================================

    @Test
    void shouldRejectSubmissionWithBlankCode() throws Exception {

        // Arrange
        UUID activityId = UUID.randomUUID();

        CreateSubmissionRequest request = new CreateSubmissionRequest(
                "",
                activityId
        );

        // Act & Assert
        mockMvc.perform(
                        post("/api/submissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }


    // ============================================================
    // POST /api/submissions
    // Validation: activityId nulo
    // ============================================================

    @Test
    void shouldRejectSubmissionWithoutActivityId() throws Exception {

        // Arrange
        CreateSubmissionRequest request = new CreateSubmissionRequest(
                "ALU-001",
                null
        );

        // Act & Assert
        mockMvc.perform(
                        post("/api/submissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}