package com.todoapp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CorsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void corsPreflight_AllowedOrigin() throws Exception {
        mockMvc.perform(options("/api/users")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", "POST"))
                .andExpect(header().string("Access-Control-Allow-Headers", "content-type"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void corsPreflight_DisallowedOrigin() throws Exception {
        mockMvc.perform(options("/api/users")
                .header("Origin", "http://malicious-site.com")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isForbidden());
    }

    @Test
    void corsActualRequest_AllowedOrigin() throws Exception {
        mockMvc.perform(options("/api/users")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }
} 