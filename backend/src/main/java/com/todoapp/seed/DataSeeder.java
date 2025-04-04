package com.todoapp.seed;

import com.todoapp.model.Todo;
import com.todoapp.model.User;
import com.todoapp.repository.TodoRepository;
import com.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        logger.info("Starting data seeding...");
        // Only seed if no users exist
        if (userRepository.count() == 0) {
            logger.info("No users found, creating test user and todos...");
            // Create test user
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setEmail("test@example.com");
            userRepository.save(testUser);
            logger.info("Test user created successfully");

            // Create sample todos
            Todo todo1 = createTodo("Project Planning", "HIGH", 0, null, testUser);
            Todo todo2 = createTodo("Research", "MEDIUM", 1, null, testUser);
            Todo todo3 = createTodo("Market Analysis", "HIGH", 2, null, testUser);
            Todo todo4 = createTodo("Competitor Research", "MEDIUM", 3, todo1, testUser);
            Todo todo5 = createTodo("User Interviews", "HIGH", 4, todo1, testUser);
            Todo todo6 = createTodo("Design", "MEDIUM", 5, null, testUser);
            Todo todo7 = createTodo("UI Design", "HIGH", 6, todo5, testUser);
            Todo todo8 = createTodo("UX Design", "HIGH", 7, todo5, testUser);
            Todo todo9 = createTodo("Development", "HIGH", 8, null, testUser);
            Todo todo10 = createTodo("Frontend Development", "HIGH", 9, todo8, testUser);
            Todo todo11 = createTodo("Backend Development", "HIGH", 10, todo8, testUser);
            Todo todo12 = createTodo("Testing", "MEDIUM", 11, null, testUser);
            Todo todo13 = createTodo("Unit Tests", "HIGH", 12, todo11, testUser);
            Todo todo14 = createTodo("Integration Tests", "HIGH", 13, todo11, testUser);
            Todo todo15 = createTodo("Deployment", "HIGH", 14, null, testUser);
            Todo todo16 = createTodo("Server Setup", "HIGH", 15, todo14, testUser);
            Todo todo17 = createTodo("CI/CD Pipeline", "HIGH", 16, todo14, testUser);

            List<Todo> todos = Arrays.asList(
                todo1,
                todo2,
                todo3,
                todo4,
                todo5,
                todo6,
                todo7,
                todo8,
                todo9,
                todo10,
                todo11,
                todo12,
                todo13,
                todo14,
                todo15,
                todo16,
                todo17
            );

            todoRepository.saveAll(todos);
            logger.info("Sample todos created successfully");
        } else {
            logger.info("Users already exist, skipping data seeding");
        }
    }

    private Todo createTodo(String title, String priority, int displayOrder, Todo parent, User user) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setPriority(Todo.Priority.valueOf(priority));
        todo.setDisplayOrder(displayOrder);
        todo.setParent(parent);
        todo.setUser(user);
        todo.setCompleted(false);
        return todo;
    }
} 