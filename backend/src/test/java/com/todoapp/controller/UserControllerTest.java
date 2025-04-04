package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.CreateUserDTO;
import com.todoapp.dto.UserDTO;
import com.todoapp.model.User;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDTO testUserDTO;
    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setEmail("test@example.com");

        createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setEmail("test@example.com");
        createUserDTO.setPassword("password123");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_ValidInput_ReturnsCreatedUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_ValidInput_ReturnsUpdatedUser() throws Exception {
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/users/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUser_ExistingId_ReturnsUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByUsername_ExistingUsername_ReturnsUser() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByEmail_ExistingEmail_ReturnsUser() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ReturnsListOfUsers() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testUser.getId()))
                .andExpect(jsonPath("$[0].username").value(testUser.getUsername()))
                .andExpect(jsonPath("$[0].email").value(testUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }
} 