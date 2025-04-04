package com.todoapp.service.impl;

import com.todoapp.exception.EntityNotFoundException;
import com.todoapp.model.Todo;
import com.todoapp.model.User;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Todo> getAllTodosByUser(User user) {
        return todoRepository.findByUserOrderByDisplayOrderAsc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Todo getTodo(Long id, User user) {
        return todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Todo not found with id: " + id));
    }

    @Override
    @Transactional
    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    @Transactional
    public Todo updateTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    @Override
    @Transactional
    public void deleteTodo(Long id, User user) {
        Todo todo = getTodo(id, user);
        todoRepository.delete(todo);
    }
} 