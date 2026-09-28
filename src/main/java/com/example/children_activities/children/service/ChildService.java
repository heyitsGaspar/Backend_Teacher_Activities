package com.example.children_activities.children.service;

import com.example.children_activities.children.dto.ChildResponse;
import com.example.children_activities.children.dto.CreateChildRequest;
import com.example.children_activities.children.entity.Child;
import com.example.children_activities.children.repository.ChildRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChildService {

    private final ChildRepository childRepository;

    public ChildService(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    @Transactional
    public ChildResponse create(CreateChildRequest request) {

        String code = generateUniqueCode();

        Child child = Child.builder()
                .name(request.name())
                .code(code)
                .build();

        Child savedChild = childRepository.save(child);

        return ChildResponse.fromEntity(savedChild);
    }

    @Transactional(readOnly = true)
    public List<ChildResponse> findAll() {

        return childRepository.findAll()
                .stream()
                .map(ChildResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChildResponse findById(UUID id) {

        Child child = childRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Alumno no encontrado")
                );

        return ChildResponse.fromEntity(child);
    }

    private String generateUniqueCode() {

        String code;

        do {
            code = "ALU-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 5)
                    .toUpperCase();

        } while (childRepository.existsByCode(code));

        return code;
    }
}