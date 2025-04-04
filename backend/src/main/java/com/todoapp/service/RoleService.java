package com.todoapp.service;

import com.todoapp.model.Role;
import com.todoapp.model.ERole;
import java.util.List;

public interface RoleService {
    // Basic CRUD operations
    Role createRole(Role role);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
    Role getRole(Long id);
    
    // Role management
    List<Role> getAllRoles();
    Role findByName(ERole name);
} 