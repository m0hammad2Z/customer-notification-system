package com.digitinarytask.customer.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("projectName", "Customer & Notification System")
                .withDetail("version", "1.0.0")
                .withDetail("latestBuildDate", "2023-10-01")
                .build();
    }
}
