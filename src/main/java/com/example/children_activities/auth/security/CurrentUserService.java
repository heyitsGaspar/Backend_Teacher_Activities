package com.example.children_activities.auth.security;

import com.example.children_activities.auth.entity.User;
import com.example.children_activities.auth.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado.
     */
    public UUID getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return UUID.fromString(authentication.getName());
    }

    /**
     * Obtiene la entidad User del usuario actualmente autenticado.
     */
    public User getCurrentUser() {

        UUID userId = getCurrentUserId();

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario autenticado no encontrado")
                );
    }
}