package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.CreateTodoDTO;
import com.todoapp.dto.TodoDTO;
import com.todoapp.model.Todo;
import com.todoapp.model.User;
import com.todoapp.security.UserPrincipal;
import com.todoapp.service.TodoService;
import com.todoapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Todo testTodo;
    private TodoDTO testTodoDTO;
    private CreateTodoDTO createTodoDTO;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");

        userPrincipal = UserPrincipal.create(testUser);

        testTodo = new Todo();
        testTodo.setId(1L);
        testTodo.setTitle("Test Todo");
        testTodo.setDescription("Test Description");
        testTodo.setCompleted(false);
        testTodo.setPriority(Todo.Priority.MEDIUM);
        testTodo.setDueDate(LocalDateTime.now());
        testTodo.setDisplayOrder(1);
        testTodo.setUser(testUser);

        testTodoDTO = TodoDTO.fromEntity(testTodo);

        createTodoDTO = new CreateTodoDTO();
        createTodoDTO.setTitle("Test Todo");
        createTodoDTO.setDescription("Test Description");
        createTodoDTO.setPriority(Todo.Priority.MEDIUM);
        createTodoDTO.setDueDate(LocalDateTime.now());
        createTodoDTO.setDisplayOrder(1);

        // Set up default mock responses
        when(userService.findByUsername("testuser")).thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getAllTodos_ReturnsListOfTodos() throws Exception {
        List<Todo> todos = Arrays.asList(testTodo);
        when(todoService.getAllTodosByUser(testUser)).thenReturn(todos);

        mockMvc.perform(get("/api/todos")
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTodo.getId()))
                .andExpect(jsonPath("$[0].title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTodo.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getTodo_ReturnsTodo() throws Exception {
        when(todoService.getTodo(1L, testUser)).thenReturn(testTodo);

        mockMvc.perform(get("/api/todos/1")
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId()))
                .andExpect(jsonPath("$.title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$.description").value(testTodo.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createTodo_ReturnsCreatedTodo() throws Exception {
        when(todoService.createTodo(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(post("/api/todos")
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTodoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId()))
                .andExpect(jsonPath("$.title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$.description").value(testTodo.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateTodo_ReturnsUpdatedTodo() throws Exception {
        when(todoService.getTodo(1L, testUser)).thenReturn(testTodo);
        when(todoService.updateTodo(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(put("/api/todos/1")
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTodoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId()))
                .andExpect(jsonPath("$.title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$.description").value(testTodo.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteTodo_ReturnsNoContent() throws Exception {
        when(todoService.getTodo(1L, testUser)).thenReturn(testTodo);

        mockMvc.perform(delete("/api/todos/1")
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateDisplayOrder_ReturnsUpdatedTodo() throws Exception {
        when(todoService.getTodo(1L, testUser)).thenReturn(testTodo);
        when(todoService.updateTodo(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(put("/api/todos/1/display-order")
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId()))
                .andExpect(jsonPath("$.displayOrder").value(1));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateParent_ReturnsUpdatedTodo() throws Exception {
        Todo parentTodo = new Todo();
        parentTodo.setId(2L);
        parentTodo.setUser(testUser);

        when(todoService.getTodo(1L, testUser)).thenReturn(testTodo);
        when(todoService.getTodo(2L, testUser)).thenReturn(parentTodo);
        when(todoService.updateTodo(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(put("/api/todos/1/parent")
                .with(SecurityMockMvcRequestPostProcessors.user(userPrincipal))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId()));
    }
} 