package com.todoapp.service;

import com.todoapp.model.Todo;
import com.todoapp.model.User;
import java.util.List;

public interface TodoService {
    List<Todo> getAllTodosByUser(User user);
    Todo getTodo(Long id, User user);
    Todo createTodo(Todo todo);
    Todo updateTodo(Todo todo);
    void deleteTodo(Long id, User user);
} 