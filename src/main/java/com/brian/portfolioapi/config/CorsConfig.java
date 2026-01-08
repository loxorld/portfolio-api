package com.brian.portfolioapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Local (dev)
                .allowedOrigins("http://localhost:3000")
                // Métodos que usás
                .allowedMethods("GET")
                // Headers típicos
                .allowedHeaders("*")
                // Cache del preflight
                .maxAge(3600);
    }
}
