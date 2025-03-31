package com.amaris.employee_management;

import com.amaris.employee_management.config.FeignRateLimitInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeignRateLimitInterceptorTest {

    @InjectMocks
    private FeignRateLimitInterceptor interceptor;

    private RequestTemplate requestTemplate;

    @BeforeEach
    void setUp() {
        requestTemplate = new RequestTemplate();

        // Replace the rateLimiter with our test semaphore
        ReflectionTestUtils.setField(interceptor, "rateLimiter", new Semaphore(2));
    }

    @Test
    @DisplayName("Apply should acquire permit when available")
    void apply_WhenPermitAvailable_ShouldAcquirePermit() {
        // Act
        interceptor.apply(requestTemplate);

        // Assert
        Semaphore semaphore = (Semaphore) ReflectionTestUtils.getField(interceptor, "rateLimiter");
        assertEquals(1, semaphore.availablePermits());
    }

    @Test
    @DisplayName("Apply should continue even when no permits available")
    void apply_WhenNoPermitAvailable_ShouldContinue() {
        // Arrange - exhaust all permits
        Semaphore semaphore = (Semaphore) ReflectionTestUtils.getField(interceptor, "rateLimiter");
        semaphore.acquireUninterruptibly(2);

        // Act - this won't throw an exception, but will proceed with zero permits
        interceptor.apply(requestTemplate);

        // Assert - should still be at 0 permits
        assertEquals(0, semaphore.availablePermits());
    }

    @Test
    @DisplayName("Release permit should increase available permits")
    void releasePermit_ShouldIncreaseAvailablePermits() {
        // Arrange - acquire one permit
        interceptor.apply(requestTemplate);
        Semaphore semaphore = (Semaphore) ReflectionTestUtils.getField(interceptor, "rateLimiter");
        assertEquals(1, semaphore.availablePermits());

        // Act
        interceptor.releasePermit();

        // Assert
        assertEquals(2, semaphore.availablePermits());
    }

    @Test
    @DisplayName("Release permit should increment available permits")
    void releasePermit_ShouldIncrementAvailablePermits() {
        // Arrange - semaphore already has max permits
        Semaphore semaphore = (Semaphore) ReflectionTestUtils.getField(interceptor, "rateLimiter");
        int initialPermits = semaphore.availablePermits();

        // Act
        interceptor.releasePermit();

        // Assert - should increase by one
        assertEquals(initialPermits + 1, semaphore.availablePermits());
    }

    @Test
    @DisplayName("Multiple acquires and releases should work correctly")
    void multipleAcquireAndRelease_ShouldWorkCorrectly() {
        // Arrange
        Semaphore semaphore = (Semaphore) ReflectionTestUtils.getField(interceptor, "rateLimiter");
        assertEquals(2, semaphore.availablePermits());

        // Act & Assert - First acquire
        interceptor.apply(requestTemplate);
        assertEquals(1, semaphore.availablePermits());

        // Act & Assert - Second acquire
        interceptor.apply(requestTemplate);
        assertEquals(0, semaphore.availablePermits());

        // Act & Assert - First release
        interceptor.releasePermit();
        assertEquals(1, semaphore.availablePermits());

        // Act & Assert - Third acquire
        interceptor.apply(requestTemplate);
        assertEquals(0, semaphore.availablePermits());

        // Act & Assert - Release all
        interceptor.releasePermit();
        interceptor.releasePermit();
        assertEquals(2, semaphore.availablePermits());
    }
}