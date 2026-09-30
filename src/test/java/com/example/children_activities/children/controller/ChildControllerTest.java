package com.example.children_activities.children.controller;

import com.example.children_activities.children.dto.ChildResponse;
import com.example.children_activities.children.dto.CreateChildRequest;
import com.example.children_activities.children.service.ChildService;
import com.example.children_activities.exception.ChildNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.children_activities.auth.security.CustomUserDetailsService;
import com.example.children_activities.auth.security.JwtService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ChildController.class)
class ChildControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ChildService childService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    // =========================================================
    // POST /api/children
    // =========================================================

    @Test
    void shouldCreateChildSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 30, 10, 30);

        CreateChildRequest request =
                new CreateChildRequest("Juan Pérez");

        ChildResponse response =
                new ChildResponse(
                        id,
                        "Juan Pérez",
                        "ALU-12345",
                        createdAt
                );

        when(childService.create(any(CreateChildRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/children")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.code").value("ALU-12345"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(childService)
                .create(any(CreateChildRequest.class));
    }


    // =========================================================
    // GET /api/children
    // =========================================================

    @Test
    void shouldFindAllChildrenSuccessfully() throws Exception {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        LocalDateTime createdAt1 =
                LocalDateTime.of(2026, 9, 30, 10, 30);

        LocalDateTime createdAt2 =
                LocalDateTime.of(2026, 9, 30, 11, 0);

        List<ChildResponse> responses = List.of(

                new ChildResponse(
                        id1,
                        "Juan Pérez",
                        "ALU-12345",
                        createdAt1
                ),

                new ChildResponse(
                        id2,
                        "María López",
                        "ALU-67890",
                        createdAt2
                )
        );

        when(childService.findAll())
                .thenReturn(responses);

        mockMvc.perform(
                        get("/api/children")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(
                        jsonPath("$[0].id")
                                .value(id1.toString())
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Juan Pérez")
                )
                .andExpect(
                        jsonPath("$[0].code")
                                .value("ALU-12345")
                )

                .andExpect(
                        jsonPath("$[1].id")
                                .value(id2.toString())
                )
                .andExpect(
                        jsonPath("$[1].name")
                                .value("María López")
                )
                .andExpect(
                        jsonPath("$[1].code")
                                .value("ALU-67890")
                );

        verify(childService).findAll();
    }


    // =========================================================
    // GET /api/children/{id}
    // =========================================================

    @Test
    void shouldFindChildByIdSuccessfully() throws Exception {

        UUID id = UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 9, 30, 10, 30);

        ChildResponse response =
                new ChildResponse(
                        id,
                        "Juan Pérez",
                        "ALU-12345",
                        createdAt
                );

        when(childService.findById(id))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/children/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(id.toString())
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Juan Pérez")
                )
                .andExpect(
                        jsonPath("$.code")
                                .value("ALU-12345")
                )
                .andExpect(
                        jsonPath("$.createdAt")
                                .exists()
                );

        verify(childService)
                .findById(id);
    }


    // =========================================================
    // GET /api/children/{id} - NOT FOUND
    // =========================================================

    @Test
    void shouldReturnNotFoundWhenChildDoesNotExist()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(childService.findById(id))
                .thenThrow(
                        new ChildNotFoundException(
                                "Alumno no encontrado"
                        )
                );

        mockMvc.perform(
                        get("/api/children/{id}", id)
                )
                .andExpect(status().isNotFound());

        verify(childService)
                .findById(id);
    }


    // =========================================================
    // POST /api/children - VALIDATION
    // =========================================================

    @Test
    void shouldRejectCreateChildWithBlankName()
            throws Exception {

        CreateChildRequest request =
                new CreateChildRequest("");

        mockMvc.perform(
                        post("/api/children")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }
}