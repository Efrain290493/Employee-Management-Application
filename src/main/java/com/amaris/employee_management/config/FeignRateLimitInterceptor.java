package com.amaris.employee_management.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Interceptor that limits the number of simultaneous calls to the external API.
 * This helps prevent reaching the API provider's rate limits.
 *
 * @author [Your Name]
 * @version 1.0
 * @since [Current Date]
 */
@Component
@Slf4j
public class FeignRateLimitInterceptor implements RequestInterceptor {

    /** Allows only 5 simultaneous API calls */
    private final Semaphore rateLimiter = new Semaphore(5);

    /** Maximum wait time to acquire a permit (2 seconds) */
    private static final long TIMEOUT_MS = 2000;

    /**
     * Attempts to acquire a permit before making an API call.
     *
     * @param template The request template for the API call
     */
    @Override
    public void apply(RequestTemplate template) {
        boolean acquired = false;
        try {
            log.debug("Attempting to acquire permit for API call: {}", template.url());
            acquired = rateLimiter.tryAcquire(TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (!acquired) {
                log.warn("Could not acquire permit for API call after {}ms: {}",
                        TIMEOUT_MS, template.url());
                // Will be allowed to continue, but likely to fail with 429
            } else {
                log.debug("Permit acquired for API call: {}", template.url());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for API call permit", e);
        }
    }

    /**
     * Method called after the request is completed to release the permit.
     * This method should be called in a finally block from the service.
     */
    public void releasePermit() {
        rateLimiter.release();
        log.debug("Permit released for API call");
    }
}