package com.Fulbito.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.Fulbito.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // Configuración automática de Spring Boot
}
