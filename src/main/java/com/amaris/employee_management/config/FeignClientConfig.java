package com.amaris.employee_management.config;
import com.amaris.employee_management.exception.FeignErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign Client with custom error handling and logging.
 *
 * Provides bean definitions for Feign client customization,
 * including logging level and error decoding.
 *
 * @author Efrain Lopez
 * @version 1.0
 * @since 31-03-2025
 */
@Configuration
public class FeignClientConfig {
    /**
     * Configures the logging level for Feign clients.
     *
     * @return Logging level (BASIC in this configuration)
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Provides a custom error decoder for Feign client exceptions.
     *
     * @return Custom ErrorDecoder implementation
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}