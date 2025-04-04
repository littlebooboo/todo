package com.todoapp.service;

import com.todoapp.model.Project;
import java.util.List;

public interface ProjectService {
    // Basic CRUD operations
    Project createProject(Project project);
    Project updateProject(Long id, Project project);
    void deleteProject(Long id);
    Project getProject(Long id);
    
    // Query operations
    List<Project> getAllProjects();
    List<Project> searchProjectsByName(String name);
    
    // Project statistics
    long getProjectTodoCount(Long projectId);
    long getProjectCompletedTodoCount(Long projectId);
} 