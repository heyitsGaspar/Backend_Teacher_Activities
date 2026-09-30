package com.example.children_activities.auth.controller;

import com.example.children_activities.auth.dto.AuthResponse;
import com.example.children_activities.auth.dto.LoginRequest;
import com.example.children_activities.auth.dto.RefreshTokenRequest;
import com.example.children_activities.auth.dto.RegisterRequest;
import com.example.children_activities.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.any;
import com.example.children_activities.auth.security.CustomUserDetailsService;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.children_activities.auth.security.JwtService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void shouldRegisterSuccessfully() throws Exception {

        RegisterRequest request = new RegisterRequest(
                "Juan Pérez",
                "juan@gmail.com",
                "password123"
        );

        AuthResponse response = new AuthResponse(
                "access-token",
                "refresh-token"
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);


        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken")
                        .value("access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("refresh-token"));


        verify(authService).register(any(RegisterRequest.class));
    }


    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest(
                "juan@gmail.com",
                "password123"
        );

        AuthResponse response = new AuthResponse(
                "access-token",
                "refresh-token"
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);


        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("refresh-token"));


        verify(authService).login(any(LoginRequest.class));
    }


    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {

        RefreshTokenRequest request =
                new RefreshTokenRequest("refresh-token");

        AuthResponse response = new AuthResponse(
                "new-access-token",
                "new-refresh-token"
        );

        when(authService.refresh("refresh-token"))
                .thenReturn(response);


        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("new-refresh-token"));


        verify(authService).refresh("refresh-token");
    }


    @Test
    void shouldRejectRegisterWithInvalidEmail() throws Exception {

        RegisterRequest request = new RegisterRequest(
                "Juan Pérez",
                "correo-invalido",
                "password123"
        );


        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRejectRegisterWithShortPassword() throws Exception {

        RegisterRequest request = new RegisterRequest(
                "Juan Pérez",
                "juan@gmail.com",
                "123"
        );


        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRejectLoginWithBlankEmail() throws Exception {

        LoginRequest request = new LoginRequest(
                "",
                "password123"
        );


        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRejectLoginWithInvalidEmail() throws Exception {

        LoginRequest request = new LoginRequest(
                "correo-invalido",
                "password123"
        );


        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRejectRefreshWithBlankToken() throws Exception {

        RefreshTokenRequest request =
                new RefreshTokenRequest("");


        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}