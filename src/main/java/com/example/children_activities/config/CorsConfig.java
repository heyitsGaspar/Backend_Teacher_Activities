package com.example.children_activities.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Configuración CORS para permitir
     * la comunicación entre Next.js y Spring Boot.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        /*
         * Permitimos únicamente nuestro frontend.
         *
         * Next.js se ejecutará en localhost:3000
         */
        configuration.setAllowedOrigins(
                List.of("http://localhost:3000")
        );

        /*
         * Métodos HTTP permitidos.
         */
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        /*
         * Permitimos los headers enviados
         * por el frontend.
         */
        configuration.setAllowedHeaders(
                List.of("*")
        );

        /*
         * IMPORTANTE:
         *
         * Permite que el navegador envíe
         * cookies al backend.
         */
        configuration.setAllowCredentials(true);

        /*
         * Aplicamos esta configuración
         * a todos los endpoints.
         */
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}