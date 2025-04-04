package com.todoapp.service.impl;

import com.todoapp.model.Project;
import com.todoapp.model.Todo;
import com.todoapp.repository.ProjectRepository;
import com.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.todoapp.exception.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project testProject;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");

        testTodo = new Todo();
        testTodo.setId(1L);
        testTodo.setTitle("Test Todo");
        testTodo.setProject(testProject);
        testTodo.setCompleted(false);
    }

    @Test
    void createProject_Success() {
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        Project result = projectService.createProject(testProject);

        assertNotNull(result);
        assertEquals(testProject.getName(), result.getName());
        assertEquals(testProject.getDescription(), result.getDescription());
        verify(projectRepository).save(testProject);
    }

    @Test
    void updateProject_Success() {
        Project updatedProject = new Project();
        updatedProject.setName("Updated Project");
        updatedProject.setDescription("Updated Description");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        Project result = projectService.updateProject(1L, updatedProject);

        assertNotNull(result);
        assertEquals(updatedProject.getName(), result.getName());
        assertEquals(updatedProject.getDescription(), result.getDescription());
        verify(projectRepository).findById(1L);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void updateProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            projectService.updateProject(1L, new Project()));
        verify(projectRepository).findById(1L);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void deleteProject_Success() {
        List<Todo> todos = Arrays.asList(testTodo);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(todoRepository.findByProjectId(1L)).thenReturn(todos);

        projectService.deleteProject(1L);

        verify(projectRepository).findById(1L);
        verify(todoRepository).findByProjectId(1L);
        verify(todoRepository).delete(testTodo);
        verify(projectRepository).delete(testProject);
    }

    @Test
    void getProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        Project result = projectService.getProject(1L);

        assertNotNull(result);
        assertEquals(testProject.getName(), result.getName());
        assertEquals(testProject.getDescription(), result.getDescription());
        verify(projectRepository).findById(1L);
    }

    @Test
    void getProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            projectService.getProject(1L));
        verify(projectRepository).findById(1L);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getProjectTodoCount_Success() {
        List<Todo> todos = Arrays.asList(testTodo);
        when(todoRepository.findByProjectId(1L)).thenReturn(todos);

        long count = projectService.getProjectTodoCount(1L);

        assertEquals(1, count);
        verify(todoRepository).findByProjectId(1L);
    }

    @Test
    void getProjectCompletedTodoCount_Success() {
        testTodo.setCompleted(true);
        List<Todo> todos = Arrays.asList(testTodo);
        when(todoRepository.findByProjectId(1L)).thenReturn(todos);

        long count = projectService.getProjectCompletedTodoCount(1L);

        assertEquals(1, count);
        verify(todoRepository).findByProjectId(1L);
    }

    @Test
    void searchProjectsByName_Success() {
        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findByNameContainingIgnoreCase("Test")).thenReturn(projects);

        List<Project> result = projectService.searchProjectsByName("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.getName(), result.get(0).getName());
        verify(projectRepository).findByNameContainingIgnoreCase("Test");
    }
} 