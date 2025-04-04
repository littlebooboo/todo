package com.todoapp.dto;

import com.todoapp.model.User;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
} 