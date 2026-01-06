package com.brian.portfolioapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class PortfolioApiApplication {

    public static void main(String[] args) {

        // 🔒 FORZAMOS TIMEZONE ANTES DE CUALQUIER CONEXIÓN
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(PortfolioApiApplication.class, args);
    }
}
