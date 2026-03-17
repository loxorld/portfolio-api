package com.brian.portfolioapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI portfolioOpenApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("adminToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-Admin-Token")
                                        .description("Admin token required for /api/admin/** endpoints")))
                .info(new Info()
                        .title("Brian Portfolio API")
                        .version("v1")
                        .description("Public API for portfolio projects (read-only)."));
    }
}
