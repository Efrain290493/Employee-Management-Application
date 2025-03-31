package com.amaris.employee_management.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration to enable retries on Feign calls.
 *
 * Provides a retry mechanism with exponential backoff to improve
 * resilience when interacting with external services.
 *
 * @author [Your Name]
 * @version 1.0
 * @since [Current Date]
 */
@Configuration
public class FeignRetryConfig {

    /**
     * Configures the retry policy for Feign clients.
     * Uses exponential backoff to avoid overwhelming the service.
     *
     * @return Retry configuration with exponential backoff
     */
    @Bean
    public Retryer retryer() {
        // Retry up to 3 times, with an initial period of 100ms and a maximum period of 1s
        return new Retryer.Default(100, TimeUnit.MILLISECONDS.toMillis(1000), 3);
    }
}