package com.todoapp.repository;

import com.todoapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Find project by name (case-insensitive)
    List<Project> findByNameContainingIgnoreCase(String name);
    
    // Find all projects ordered by creation date
    List<Project> findAllByOrderByCreatedAtDesc();
} 