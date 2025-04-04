package com.todoapp.service.impl;

import com.todoapp.exception.EntityNotFoundException;
import com.todoapp.model.ERole;
import com.todoapp.model.Role;
import com.todoapp.model.User;
import com.todoapp.repository.RoleRepository;
import com.todoapp.repository.UserRepository;
import com.todoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Assign default user role
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Default user role not found"));
        user.getRoles().add(userRole);
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        user.setEmail(userDetails.getEmail());
        // Note: Password updates should be handled through changePassword method
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUser(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
} 