package com.todoapp.dto;

import com.todoapp.model.Todo;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CreateTodoDTO {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Priority is required")
    private Todo.Priority priority;

    private LocalDateTime dueDate;

    private Long parentId;

    private Long projectId;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
} 