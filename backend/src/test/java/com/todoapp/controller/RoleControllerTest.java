package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.dto.CreateRoleDTO;
import com.todoapp.dto.RoleDTO;
import com.todoapp.model.Role;
import com.todoapp.model.ERole;
import com.todoapp.service.RoleService;
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

@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role testRole;
    private RoleDTO testRoleDTO;
    private CreateRoleDTO createRoleDTO;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName(ERole.ROLE_USER);

        testRoleDTO = new RoleDTO();
        testRoleDTO.setId(1L);
        testRoleDTO.setName(ERole.ROLE_USER);

        createRoleDTO = new CreateRoleDTO();
        createRoleDTO.setName(ERole.ROLE_USER);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRole_ValidInput_ReturnsCreatedRole() throws Exception {
        when(roleService.createRole(any(Role.class))).thenReturn(testRole);

        mockMvc.perform(post("/api/roles")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRole.getId()))
                .andExpect(jsonPath("$.name").value(testRole.getName().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRole_ValidInput_ReturnsUpdatedRole() throws Exception {
        when(roleService.updateRole(eq(1L), any(Role.class))).thenReturn(testRole);

        mockMvc.perform(put("/api/roles/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRoleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRole.getId()))
                .andExpect(jsonPath("$.name").value(testRole.getName().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRole_ExistingId_ReturnsRole() throws Exception {
        when(roleService.getRole(1L)).thenReturn(testRole);

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRole.getId()))
                .andExpect(jsonPath("$.name").value(testRole.getName().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRoles_ReturnsListOfRoles() throws Exception {
        List<Role> roles = Arrays.asList(testRole);
        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testRole.getId()))
                .andExpect(jsonPath("$[0].name").value(testRole.getName().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoleByName_ExistingName_ReturnsRole() throws Exception {
        when(roleService.findByName(ERole.ROLE_USER)).thenReturn(testRole);

        mockMvc.perform(get("/api/roles/name/ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRole.getId()))
                .andExpect(jsonPath("$.name").value(testRole.getName().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoleByName_InvalidName_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/roles/name/INVALID_ROLE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRole_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/roles/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }
} 