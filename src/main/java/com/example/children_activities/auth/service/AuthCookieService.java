package com.example.children_activities.auth.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthCookieService {

    /**
     * Crea la cookie que contiene el Access Token.
     */
    public ResponseCookie createAccessTokenCookie(
            String accessToken
    ) {

        return ResponseCookie
                .from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // En desarrollo usamos HTTP
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();
    }

    /**
     * Crea la cookie que contiene el Refresh Token.
     */
    public ResponseCookie createRefreshTokenCookie(
            String refreshToken
    ) {

        return ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
    }

    /**
     * Elimina la cookie del Access Token.
     */
    public ResponseCookie deleteAccessTokenCookie() {

        return ResponseCookie
                .from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }

    /**
     * Elimina la cookie del Refresh Token.
     */
    public ResponseCookie deleteRefreshTokenCookie() {

        return ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}