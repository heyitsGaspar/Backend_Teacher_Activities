package com.example.children_activities.auth.controller;

import com.example.children_activities.auth.dto.AuthResponse;
import com.example.children_activities.auth.dto.LoginRequest;
import com.example.children_activities.auth.dto.RegisterRequest;
import com.example.children_activities.auth.service.AuthCookieService;
import com.example.children_activities.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;

    public AuthController(
            AuthService authService,
            AuthCookieService authCookieService
    ) {
        this.authService = authService;
        this.authCookieService = authCookieService;
    }

    /**
     * Registra un nuevo usuario.
     *
     * Los JWT generados por AuthService
     * se almacenan en cookies HttpOnly.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        AuthResponse auth =
                authService.register(request);

        ResponseCookie accessCookie =
                authCookieService.createAccessTokenCookie(
                        auth.accessToken()
                );

        ResponseCookie refreshCookie =
                authCookieService.createRefreshTokenCookie(
                        auth.refreshToken()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(
                        HttpHeaders.SET_COOKIE,
                        accessCookie.toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )
                .body(
                        "Usuario registrado correctamente"
                );
    }

    /**
     * Autentica un usuario.
     *
     * Los JWT se envían mediante cookies HttpOnly.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse auth =
                authService.login(request);

        ResponseCookie accessCookie =
                authCookieService.createAccessTokenCookie(
                        auth.accessToken()
                );

        ResponseCookie refreshCookie =
                authCookieService.createRefreshTokenCookie(
                        auth.refreshToken()
                );

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        accessCookie.toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )
                .body(
                        "Inicio de sesión exitoso"
                );
    }

    /**
     * Genera un nuevo Access Token
     * utilizando el Refresh Token almacenado
     * en una cookie HttpOnly.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(
                    name = "refreshToken",
                    required = false
            )
            String refreshToken
    ) {

        if (refreshToken == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "Refresh token no encontrado"
                    );
        }

        AuthResponse auth =
                authService.refresh(refreshToken);

        ResponseCookie accessCookie =
                authCookieService.createAccessTokenCookie(
                        auth.accessToken()
                );

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        accessCookie.toString()
                )
                .body(
                        "Access token renovado"
                );
    }

    /**
     * Cierra la sesión eliminando
     * las cookies de autenticación.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie accessCookie =
                authCookieService.deleteAccessTokenCookie();

        ResponseCookie refreshCookie =
                authCookieService.deleteRefreshTokenCookie();

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        accessCookie.toString()
                )
                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )
                .body(
                        "Sesión cerrada correctamente"
                );
    }
}