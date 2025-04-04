package com.todoapp.service.impl;

import com.todoapp.model.ERole;
import com.todoapp.model.Role;
import com.todoapp.model.User;
import com.todoapp.repository.RoleRepository;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.todoapp.exception.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(ERole.ROLE_USER);
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains(userRole));
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByName(ERole.ROLE_USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_UsernameTaken() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser(testUser));
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailInUse() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser(testUser));
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_Success() {
        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(1L, updatedUser);

        assertNotNull(result);
        assertEquals(updatedUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            userService.updateUser(1L, new User()));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void getUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            userService.getUser(1L));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_NotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            userService.findByUsername("testuser"));
        verify(userRepository).findByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        User result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_NotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            userService.findByEmail("test@example.com"));
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void existsByUsername_Success() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByEmail_Success() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void changePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "password123")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(1L, "oldPassword", "newPassword");

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("oldPassword", "password123");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(any(User.class));
        assertEquals("encodedNewPassword", testUser.getPassword());
    }

    @Test
    void changePassword_IncorrectOldPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.changePassword(1L, "wrongPassword", "newPassword"));
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("wrongPassword", testUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }
} 