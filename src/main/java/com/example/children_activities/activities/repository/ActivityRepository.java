package com.example.children_activities.activities.repository;
import com.example.children_activities.activities.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

}