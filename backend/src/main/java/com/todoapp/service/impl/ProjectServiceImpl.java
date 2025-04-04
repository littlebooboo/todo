package com.todoapp.service.impl;

import com.todoapp.exception.EntityNotFoundException;
import com.todoapp.model.Project;
import com.todoapp.model.Todo;
import com.todoapp.repository.ProjectRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Override
    @Transactional
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public Project updateProject(Long id, Project projectDetails) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        
        // Delete all todo items associated with the project
        List<Todo> todos = todoRepository.findByProjectId(id);
        todos.forEach(todo -> todoRepository.delete(todo));
        
        projectRepository.delete(project);
    }

    @Override
    public Project getProject(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> searchProjectsByName(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public long getProjectTodoCount(Long projectId) {
        return todoRepository.findByProjectId(projectId).size();
    }

    @Override
    public long getProjectCompletedTodoCount(Long projectId) {
        return todoRepository.findByProjectId(projectId).stream()
            .filter(Todo::isCompleted)
            .count();
    }
} 