package com.amaris.employee_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for caching employee-related data.
 *
 * Enables caching and configures cache manager with
 * specified maximum size and expiration time.
 *
 * @author Efrain Lopez
 * @version 1.0
 * @since 31-03-2025
 */
@Configuration
@EnableCaching
public class CacheConfig {
    /** Maximum number of entries in the cache */
    @Value("${cache.employees.max-size}")
    private int maxSize;

    /** Time after which cache entries expire */
    @Value("${cache.employees.expire-after-write-minutes}")
    private int expireAfterMinutes;

    /**
     * Creates and configures the cache manager.
     *
     * @return Configured CacheManager with 'employees' cache
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("employees")
        ));
        return cacheManager;
    }
}