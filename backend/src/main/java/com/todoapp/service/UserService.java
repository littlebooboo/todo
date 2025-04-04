package com.todoapp.service;

import com.todoapp.model.User;
import java.util.List;

public interface UserService {
    // Basic CRUD operations
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User getUser(Long id);
    
    // Authentication operations
    User findByUsername(String username);
    User findByEmail(String email);
    
    // Validation operations
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // User management
    List<User> getAllUsers();
    void changePassword(Long userId, String oldPassword, String newPassword);
} 