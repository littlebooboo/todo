package com.todoapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public CorsFilter corsFilter() {
        logger.info("Configuring CORS filter...");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow both backend and frontend origins
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:5173");
        
        // Allow all HTTP methods
        config.addAllowedMethod("*");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Apply this configuration to all paths
        source.registerCorsConfiguration("/**", config);
        
        logger.info("CORS filter configuration completed");
        return new CorsFilter(source);
    }
} 