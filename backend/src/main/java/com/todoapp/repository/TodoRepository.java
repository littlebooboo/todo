package com.todoapp.repository;

import com.todoapp.model.Todo;
import com.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserOrderByDisplayOrderAsc(User user);
    Optional<Todo> findByIdAndUser(Long id, User user);
    List<Todo> findByProjectId(Long projectId);
} 