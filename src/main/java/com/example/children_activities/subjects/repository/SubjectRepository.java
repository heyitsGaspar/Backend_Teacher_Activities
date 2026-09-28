package com.example.children_activities.subjects.repository;
import com.example.children_activities.subjects.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

}