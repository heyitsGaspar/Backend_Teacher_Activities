package com.example.children_activities.auth.security;

import com.example.children_activities.auth.entity.User;
import com.example.children_activities.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        // Verificamos que exista un token Bearer.
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el JWT eliminando "Bearer ".
        String token =
                authHeader.substring(7);

        String userId;

        try {

            // El subject del JWT ahora contiene el UUID.
            userId = jwtService.extractUsername(token);

            // Comprobamos que el subject sea un UUID válido.
            UUID.fromString(userId);

        } catch (Exception e) {

            filterChain.doFilter(request, response);
            return;
        }

        // Solo creamos la autenticación si todavía no existe.
        if (SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            UUID uuid = UUID.fromString(userId);

            User user = userRepository
                    .findById(uuid)
                    .orElse(null);

            if (user != null &&
                    jwtService.isTokenValid(token)) {

                /*
                 * IMPORTANTE:
                 *
                 * Utilizamos el UUID como principal.
                 *
                 * Esto permite que:
                 *
                 * authentication.getName()
                 *
                 * devuelva el UUID del usuario.
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                null
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}