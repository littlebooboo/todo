package com.todoapp.dto;

import com.todoapp.model.ERole;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class CreateRoleDTO {
    @NotNull(message = "Role name is required")
    private ERole name;
} 