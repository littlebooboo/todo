package com.todoapp.dto;

import com.todoapp.model.Role;
import com.todoapp.model.ERole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleDTO {
    private Long id;
    private ERole name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoleDTO fromEntity(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }
} 