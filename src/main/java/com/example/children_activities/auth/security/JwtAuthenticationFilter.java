package com.example.children_activities.auth.security;

import com.example.children_activities.auth.entity.User;
import com.example.children_activities.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

        /*
         * Buscamos el accessToken dentro de las cookies.
         *
         * Ya no utilizamos:
         *
         * Authorization: Bearer <token>
         *
         * porque ahora el JWT se almacena
         * en una cookie HttpOnly.
         */
        String token = getAccessTokenFromCookie(request);

        // Si no existe la cookie, continuamos normalmente.
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId;

        try {

            // El subject del JWT contiene el UUID del usuario.
            userId = jwtService.extractUsername(token);

            // Comprobamos que el subject sea un UUID válido.
            UUID.fromString(userId);

        } catch (Exception e) {

            // Si el token es inválido, continuamos sin autenticar.
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Solo creamos la autenticación si todavía
         * no existe una autenticación en el contexto.
         */
        if (SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            UUID uuid = UUID.fromString(userId);

            User user = userRepository
                    .findById(uuid)
                    .orElse(null);

            /*
             * El usuario debe existir y el JWT debe
             * seguir siendo válido.
             */
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

    /**
     * Busca la cookie accessToken dentro de la petición.
     *
     * @param request petición HTTP
     * @return JWT encontrado o null si no existe
     */
    private String getAccessTokenFromCookie(
            HttpServletRequest request
    ) {

        Cookie[] cookies = request.getCookies();

        // La petición puede no contener cookies.
        if (cookies == null) {
            return null;
        }

        // Buscamos específicamente la cookie accessToken.
        for (Cookie cookie : cookies) {

            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}