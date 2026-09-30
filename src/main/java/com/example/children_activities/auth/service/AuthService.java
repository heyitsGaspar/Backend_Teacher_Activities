package com.example.children_activities.auth.service;

import com.example.children_activities.auth.dto.*;
import com.example.children_activities.auth.entity.User;
import com.example.children_activities.auth.repository.UserRepository;
import com.example.children_activities.auth.security.JwtService;
import com.example.children_activities.exception.EmailAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "El email ya está registrado"
            );
        }

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordHash)
                .build();

        User savedUser = userRepository.save(user);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(savedUser.getEmail())
                        .password(savedUser.getPasswordHash())
                        .authorities("USER")
                        .build();

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .authorities("USER")
                        .build();

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

    public AuthResponse refresh(String refreshToken) {

        /*
         * Verificamos que el Refresh Token
         * sea válido y no haya expirado.
         */
        if (!jwtService.isTokenValid(refreshToken)) {

            throw new IllegalArgumentException(
                    "Refresh token inválido o expirado"
            );
        }

        /*
         * Extraemos el email del Refresh Token.
         */
        String email =
                jwtService.extractUsername(refreshToken);

        /*
         * Buscamos al usuario en la base de datos.
         */
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow();

        /*
         * Creamos UserDetails.
         */
        UserDetails userDetails =
                createUserDetails(user);

        /*
         * Generamos un nuevo Access Token.
         */
        String accessToken =
                jwtService.generateAccessToken(userDetails);

        /*
         * Devolvemos el nuevo Access Token
         * junto con el Refresh Token existente.
         */
        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }
    private UserDetails createUserDetails(User user) {

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();
    }




}