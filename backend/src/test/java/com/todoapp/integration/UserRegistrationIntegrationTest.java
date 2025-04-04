package com.todoapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.CreateUserDTO;
import com.todoapp.dto.UserDTO;
import com.todoapp.model.ERole;
import com.todoapp.model.Role;
import com.todoapp.model.User;
import com.todoapp.repository.RoleRepository;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        
        // Create default roles
        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);
        roleRepository.save(userRole);
    }

    @Test
    void registerUser_Success() throws Exception {
        // Prepare test data
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setEmail("testuser@example.com");
        createUserDTO.setPassword("password123");

        // Execute request
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Parse response
        UserDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);

        // Verify response
        assertNotNull(response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("testuser@example.com", response.getEmail());
        assertNotNull(response.getRoles());
        assertTrue(response.getRoles().contains(ERole.ROLE_USER.name()));

        // Verify database
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("testuser@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getPassword());
        assertNotEquals("password123", savedUser.getPassword()); // Password should be encrypted
    }

    @Test
    void registerUser_DuplicateUsername() throws Exception {
        // Create initial user
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setEmail("testuser@example.com");
        createUserDTO.setPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isOk());

        // Try to create user with same username
        CreateUserDTO duplicateUserDTO = new CreateUserDTO();
        duplicateUserDTO.setUsername("testuser");
        duplicateUserDTO.setEmail("another@example.com");
        duplicateUserDTO.setPassword("password456");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidEmail() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("testuser");
        createUserDTO.setEmail("invalid-email");
        createUserDTO.setPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpect(status().isBadRequest());
    }
} 