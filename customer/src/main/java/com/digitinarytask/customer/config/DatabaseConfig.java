package com.digitinarytask.customer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "springAware")
@Configuration
public class DatabaseConfig {
}
