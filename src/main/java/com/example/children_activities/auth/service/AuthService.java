package com.example.children_activities.auth.service;

import com.example.children_activities.auth.dto.AuthResponse;
import com.example.children_activities.auth.dto.LoginRequest;
import com.example.children_activities.auth.dto.RegisterRequest;
import com.example.children_activities.auth.entity.User;
import com.example.children_activities.auth.repository.UserRepository;
import com.example.children_activities.auth.security.JwtService;
import com.example.children_activities.exception.EmailAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    /**
     * Registra un nuevo usuario.
     *
     * Después de guardar el usuario se generan
     * el Access Token y el Refresh Token.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        /*
         * Verificamos que el email no esté registrado.
         */
        if (userRepository.existsByEmail(request.email())) {

            throw new EmailAlreadyExistsException(
                    "El email ya está registrado"
            );
        }

        /*
         * Encriptamos la contraseña.
         */
        String passwordHash =
                passwordEncoder.encode(
                        request.password()
                );

        /*
         * Creamos el usuario.
         */
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordHash)
                .build();

        /*
         * Guardamos el usuario.
         *
         * Aquí Hibernate genera el UUID.
         */
        User savedUser =
                userRepository.save(user);

        /*
         * Generamos los tokens utilizando
         * el UUID del usuario.
         */
        String accessToken =
                jwtService.generateAccessToken(
                        savedUser
                );

        String refreshToken =
                jwtService.generateRefreshToken(
                        savedUser
                );

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

    /**
     * Autentica un usuario mediante email
     * y contraseña.
     */
    public AuthResponse login(LoginRequest request) {

        /*
         * Spring Security valida las credenciales.
         */
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        /*
         * Obtenemos el usuario mediante su email.
         */
        User user =
                userRepository
                        .findByEmail(request.email())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Usuario no encontrado"
                                )
                        );

        /*
         * Generamos los tokens utilizando
         * el UUID del usuario.
         */
        String accessToken =
                jwtService.generateAccessToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }

    /**
     * Genera un nuevo Access Token utilizando
     * un Refresh Token válido.
     */
    public AuthResponse refresh(String refreshToken) {

        /*
         * Primero verificamos que el Refresh Token
         * tenga una firma válida y no esté expirado.
         */
        if (!jwtService.isTokenValid(refreshToken)) {

            throw new IllegalArgumentException(
                    "Refresh token inválido o expirado"
            );
        }

        /*
         * Extraemos el subject del JWT.
         *
         * Ahora contiene el UUID del usuario.
         */
        String userIdString =
                jwtService.extractUsername(
                        refreshToken
                );

        UUID userId;

        try {

            /*
             * Convertimos el subject a UUID.
             */
            userId =
                    UUID.fromString(userIdString);

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Refresh token inválido"
            );
        }

        /*
         * Buscamos al usuario mediante su UUID.
         */
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Usuario no encontrado"
                                )
                        );

        /*
         * Generamos un nuevo Access Token.
         */
        String accessToken =
                jwtService.generateAccessToken(user);

        /*
         * Devolvemos el nuevo Access Token
         * y mantenemos el Refresh Token actual.
         */
        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }
}