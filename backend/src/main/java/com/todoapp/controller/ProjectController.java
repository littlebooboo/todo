package com.todoapp.controller;

import com.todoapp.dto.CreateProjectDTO;
import com.todoapp.dto.ProjectDTO;
import com.todoapp.model.Project;
import com.todoapp.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody CreateProjectDTO createProjectDTO) {
        Project project = new Project();
        project.setName(createProjectDTO.getName());
        project.setDescription(createProjectDTO.getDescription());
        return ResponseEntity.ok(ProjectDTO.fromEntity(projectService.createProject(project)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody CreateProjectDTO createProjectDTO) {
        Project project = new Project();
        project.setName(createProjectDTO.getName());
        project.setDescription(createProjectDTO.getDescription());
        return ResponseEntity.ok(ProjectDTO.fromEntity(projectService.updateProject(id, project)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(ProjectDTO.fromEntity(projectService.getProject(id)));
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects().stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectDTO>> searchProjects(@RequestParam String name) {
        return ResponseEntity.ok(projectService.searchProjectsByName(name).stream()
                .map(ProjectDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}/todo-count")
    public ResponseEntity<Long> getProjectTodoCount(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectTodoCount(id));
    }

    @GetMapping("/{id}/completed-count")
    public ResponseEntity<Long> getProjectCompletedTodoCount(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectCompletedTodoCount(id));
    }
} 