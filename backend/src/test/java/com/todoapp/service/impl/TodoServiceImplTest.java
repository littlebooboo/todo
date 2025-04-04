package com.todoapp.service.impl;

import com.todoapp.exception.EntityNotFoundException;
import com.todoapp.model.Todo;
import com.todoapp.model.User;
import com.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private User testUser;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testTodo = new Todo();
        testTodo.setId(1L);
        testTodo.setTitle("Test Todo");
        testTodo.setDescription("Test Description");
        testTodo.setCompleted(false);
        testTodo.setPriority(Todo.Priority.MEDIUM);
        testTodo.setDueDate(LocalDateTime.now());
        testTodo.setDisplayOrder(1);
        testTodo.setUser(testUser);
    }

    @Test
    void getAllTodosByUser_ReturnsListOfTodos() {
        List<Todo> expectedTodos = Arrays.asList(testTodo);
        when(todoRepository.findByUserOrderByDisplayOrderAsc(testUser)).thenReturn(expectedTodos);

        List<Todo> actualTodos = todoService.getAllTodosByUser(testUser);

        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository).findByUserOrderByDisplayOrderAsc(testUser);
    }

    @Test
    void getTodo_WhenTodoExists_ReturnsTodo() {
        when(todoRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTodo));

        Todo actualTodo = todoService.getTodo(1L, testUser);

        assertEquals(testTodo, actualTodo);
        verify(todoRepository).findByIdAndUser(1L, testUser);
    }

    @Test
    void getTodo_WhenTodoDoesNotExist_ThrowsException() {
        when(todoRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> todoService.getTodo(1L, testUser));
        verify(todoRepository).findByIdAndUser(1L, testUser);
    }

    @Test
    void createTodo_ReturnsCreatedTodo() {
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        Todo createdTodo = todoService.createTodo(testTodo);

        assertEquals(testTodo, createdTodo);
        verify(todoRepository).save(testTodo);
    }

    @Test
    void updateTodo_ReturnsUpdatedTodo() {
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        Todo updatedTodo = todoService.updateTodo(testTodo);

        assertEquals(testTodo, updatedTodo);
        verify(todoRepository).save(testTodo);
    }

    @Test
    void deleteTodo_DeletesTodo() {
        when(todoRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTodo));

        todoService.deleteTodo(1L, testUser);

        verify(todoRepository).findByIdAndUser(1L, testUser);
        verify(todoRepository).delete(testTodo);
    }

    @Test
    void deleteTodo_WhenTodoDoesNotExist_ThrowsException() {
        when(todoRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> todoService.deleteTodo(1L, testUser));
        verify(todoRepository).findByIdAndUser(1L, testUser);
        verify(todoRepository, never()).delete(any(Todo.class));
    }
} 