package com.todoapp.service.impl;

import com.todoapp.model.Role;
import com.todoapp.model.ERole;
import com.todoapp.repository.RoleRepository;
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
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName(ERole.ROLE_USER);
    }

    @Test
    void createRole_Success() {
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        Role result = roleService.createRole(testRole);

        assertNotNull(result);
        assertEquals(testRole.getName(), result.getName());
        verify(roleRepository).save(testRole);
    }

    @Test
    void updateRole_Success() {
        Role updatedRole = new Role();
        updatedRole.setName(ERole.ROLE_ADMIN);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        Role result = roleService.updateRole(1L, updatedRole);

        assertNotNull(result);
        assertEquals(updatedRole.getName(), result.getName());
        verify(roleRepository).findById(1L);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void updateRole_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            roleService.updateRole(1L, new Role()));
        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void deleteRole_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        roleService.deleteRole(1L);

        verify(roleRepository).findById(1L);
        verify(roleRepository).delete(testRole);
    }

    @Test
    void getRole_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));

        Role result = roleService.getRole(1L);

        assertNotNull(result);
        assertEquals(testRole.getName(), result.getName());
        verify(roleRepository).findById(1L);
    }

    @Test
    void getRole_NotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            roleService.getRole(1L));
        verify(roleRepository).findById(1L);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getAllRoles_Success() {
        List<Role> roles = Arrays.asList(testRole);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRole.getName(), result.get(0).getName());
        verify(roleRepository).findAll();
    }

    @Test
    void findByName_Success() {
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(testRole));

        Role result = roleService.findByName(ERole.ROLE_USER);

        assertNotNull(result);
        assertEquals(testRole.getName(), result.getName());
        verify(roleRepository).findByName(ERole.ROLE_USER);
    }

    @Test
    void findByName_NotFound() {
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            roleService.findByName(ERole.ROLE_USER));
        verify(roleRepository).findByName(ERole.ROLE_USER);
        verify(roleRepository, never()).save(any(Role.class));
    }
} 