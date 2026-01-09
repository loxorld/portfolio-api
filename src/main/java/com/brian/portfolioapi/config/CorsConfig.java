package com.brian.portfolioapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // Público (solo lectura desde  front)
        registry.addMapping("/api/projects/**")
                .allowedOriginPatterns(
                        "http://localhost:3000",
                        "https://portfolio-web-orpin-three.vercel.app"
                )
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);

        // Admin (solo desde Swagger en Railway)
        registry.addMapping("/api/admin/**")
                .allowedOriginPatterns(
                        "https://portfolio-brian-ladelfa.up.railway.app"
                )
                .allowedMethods("POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
