package com.todoapp.repository;

import com.todoapp.model.Todo;
import com.todoapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class TodoRepositoryIntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);

        // Create test todo
        testTodo = new Todo();
        testTodo.setTitle("Test Todo");
        testTodo.setDescription("Test Description");
        testTodo.setCompleted(false);
        testTodo.setPriority(Todo.Priority.MEDIUM);
        testTodo.setDueDate(LocalDateTime.now());
        testTodo.setDisplayOrder(0);
        testTodo.setUser(testUser);
        testTodo = todoRepository.save(testTodo);
    }

    @Test
    void findByUserOrderByDisplayOrderAsc_ReturnsTodosForUser() {
        // Create another todo for the same user
        Todo anotherTodo = new Todo();
        anotherTodo.setTitle("Another Todo");
        anotherTodo.setDescription("Another Description");
        anotherTodo.setCompleted(false);
        anotherTodo.setPriority(Todo.Priority.HIGH);
        anotherTodo.setDueDate(LocalDateTime.now());
        anotherTodo.setDisplayOrder(1);
        anotherTodo.setUser(testUser);
        todoRepository.save(anotherTodo);

        // Create a todo for a different user
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("password123");
        otherUser = userRepository.save(otherUser);

        Todo otherUserTodo = new Todo();
        otherUserTodo.setTitle("Other User Todo");
        otherUserTodo.setDescription("Other User Description");
        otherUserTodo.setCompleted(false);
        otherUserTodo.setPriority(Todo.Priority.LOW);
        otherUserTodo.setDueDate(LocalDateTime.now());
        otherUserTodo.setDisplayOrder(0);
        otherUserTodo.setUser(otherUser);
        todoRepository.save(otherUserTodo);

        List<Todo> todos = todoRepository.findByUserOrderByDisplayOrderAsc(testUser);

        assertEquals(2, todos.size());
        assertTrue(todos.contains(testTodo));
        assertTrue(todos.contains(anotherTodo));
        assertFalse(todos.contains(otherUserTodo));
    }

    @Test
    void findByIdAndUser_WhenTodoExists_ReturnsTodo() {
        Todo foundTodo = todoRepository.findByIdAndUser(testTodo.getId(), testUser)
                .orElse(null);

        assertNotNull(foundTodo);
        assertEquals(testTodo.getId(), foundTodo.getId());
        assertEquals(testTodo.getTitle(), foundTodo.getTitle());
        assertEquals(testTodo.getDescription(), foundTodo.getDescription());
        assertEquals(testTodo.getUser().getId(), foundTodo.getUser().getId());
    }

    @Test
    void findByIdAndUser_WhenTodoDoesNotExist_ReturnsEmpty() {
        Todo foundTodo = todoRepository.findByIdAndUser(999L, testUser)
                .orElse(null);

        assertNull(foundTodo);
    }

    @Test
    void findByIdAndUser_WhenUserDoesNotOwnTodo_ReturnsEmpty() {
        // Create another user
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("password123");
        otherUser = userRepository.save(otherUser);

        // Create a todo for the other user
        Todo otherUserTodo = new Todo();
        otherUserTodo.setTitle("Other User Todo");
        otherUserTodo.setDescription("Other User Description");
        otherUserTodo.setCompleted(false);
        otherUserTodo.setPriority(Todo.Priority.LOW);
        otherUserTodo.setDueDate(LocalDateTime.now());
        otherUserTodo.setDisplayOrder(0);
        otherUserTodo.setUser(otherUser);
        todoRepository.save(otherUserTodo);

        // Try to find the other user's todo with the first user
        Todo foundTodo = todoRepository.findByIdAndUser(otherUserTodo.getId(), testUser)
                .orElse(null);

        assertNull(foundTodo);
    }
} 