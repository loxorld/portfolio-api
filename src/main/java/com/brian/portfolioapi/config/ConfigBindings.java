package com.brian.portfolioapi.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AdminProperties.class,
        CorsProperties.class
})
public class ConfigBindings {}
