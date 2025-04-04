package com.todoapp.dto;

import com.todoapp.model.Todo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TodoDTO {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Todo.Priority priority;
    private LocalDateTime dueDate;
    private Long parentId;
    private Long projectId;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TodoDTO fromEntity(Todo todo) {
        TodoDTO dto = new TodoDTO();
        dto.setId(todo.getId());
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setCompleted(todo.isCompleted());
        dto.setPriority(todo.getPriority());
        dto.setDueDate(todo.getDueDate());
        dto.setParentId(todo.getParent() != null ? todo.getParent().getId() : null);
        dto.setProjectId(todo.getProject() != null ? todo.getProject().getId() : null);
        dto.setDisplayOrder(todo.getDisplayOrder());
        dto.setCreatedAt(todo.getCreatedAt());
        dto.setUpdatedAt(todo.getUpdatedAt());
        return dto;
    }
} 