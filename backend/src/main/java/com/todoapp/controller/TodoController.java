package com.todoapp.controller;

import com.todoapp.dto.CreateTodoDTO;
import com.todoapp.dto.TodoDTO;
import com.todoapp.model.Todo;
import com.todoapp.model.User;
import com.todoapp.security.UserPrincipal;
import com.todoapp.service.TodoService;
import com.todoapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<TodoDTO>> getAllTodos(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        List<Todo> todos = todoService.getAllTodosByUser(user);
        return ResponseEntity.ok(todos.stream()
                .map(TodoDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> getTodo(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        Todo todo = todoService.getTodo(id, user);
        return ResponseEntity.ok(TodoDTO.fromEntity(todo));
    }

    @PostMapping
    public ResponseEntity<TodoDTO> createTodo(@Valid @RequestBody CreateTodoDTO createTodoDTO, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        Todo todo = new Todo();
        todo.setTitle(createTodoDTO.getTitle());
        todo.setDescription(createTodoDTO.getDescription());
        todo.setPriority(createTodoDTO.getPriority());
        todo.setDueDate(createTodoDTO.getDueDate());
        todo.setDisplayOrder(createTodoDTO.getDisplayOrder());
        todo.setUser(user);
        
        if (createTodoDTO.getParentId() != null) {
            Todo parent = todoService.getTodo(createTodoDTO.getParentId(), user);
            todo.setParent(parent);
        }
        
        return ResponseEntity.ok(TodoDTO.fromEntity(todoService.createTodo(todo)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDTO> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody CreateTodoDTO createTodoDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        Todo todo = todoService.getTodo(id, user);
        todo.setTitle(createTodoDTO.getTitle());
        todo.setDescription(createTodoDTO.getDescription());
        todo.setPriority(createTodoDTO.getPriority());
        todo.setDueDate(createTodoDTO.getDueDate());
        todo.setDisplayOrder(createTodoDTO.getDisplayOrder());
        
        if (createTodoDTO.getParentId() != null) {
            Todo parent = todoService.getTodo(createTodoDTO.getParentId(), user);
            todo.setParent(parent);
        } else {
            todo.setParent(null);
        }
        
        return ResponseEntity.ok(TodoDTO.fromEntity(todoService.updateTodo(todo)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        todoService.deleteTodo(id, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/display-order")
    public ResponseEntity<TodoDTO> updateOrder(
            @PathVariable Long id,
            @RequestBody Integer displayOrder,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        Todo todo = todoService.getTodo(id, user);
        todo.setDisplayOrder(displayOrder);
        return ResponseEntity.ok(TodoDTO.fromEntity(todoService.updateTodo(todo)));
    }

    @PutMapping("/{id}/parent")
    public ResponseEntity<TodoDTO> updateParent(
            @PathVariable Long id,
            @RequestBody Long parentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.findByUsername(userPrincipal.getUsername());
        Todo todo = todoService.getTodo(id, user);
        if (parentId != null) {
            Todo parent = todoService.getTodo(parentId, user);
            todo.setParent(parent);
        } else {
            todo.setParent(null);
        }
        return ResponseEntity.ok(TodoDTO.fromEntity(todoService.updateTodo(todo)));
    }
} 