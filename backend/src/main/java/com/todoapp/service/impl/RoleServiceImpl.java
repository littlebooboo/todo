package com.todoapp.service.impl;

import com.todoapp.exception.EntityNotFoundException;
import com.todoapp.model.Role;
import com.todoapp.model.ERole;
import com.todoapp.repository.RoleRepository;
import com.todoapp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        
        role.setName(roleDetails.getName());
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        roleRepository.delete(role);
    }

    @Override
    public Role getRole(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role findByName(ERole name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + name));
    }
} 