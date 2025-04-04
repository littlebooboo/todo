package com.todoapp.controller;

import com.todoapp.dto.CreateRoleDTO;
import com.todoapp.dto.RoleDTO;
import com.todoapp.model.Role;
import com.todoapp.model.ERole;
import com.todoapp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody CreateRoleDTO createRoleDTO) {
        Role role = new Role();
        role.setName(createRoleDTO.getName());
        return ResponseEntity.ok(RoleDTO.fromEntity(roleService.createRole(role)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody CreateRoleDTO createRoleDTO) {
        Role role = new Role();
        role.setName(createRoleDTO.getName());
        return ResponseEntity.ok(RoleDTO.fromEntity(roleService.updateRole(id, role)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(RoleDTO.fromEntity(roleService.getRole(id)));
    }

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles().stream()
                .map(RoleDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {
        try {
            ERole eRole = ERole.valueOf(name.toUpperCase());
            return ResponseEntity.ok(RoleDTO.fromEntity(roleService.findByName(eRole)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role name: " + name);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
} 