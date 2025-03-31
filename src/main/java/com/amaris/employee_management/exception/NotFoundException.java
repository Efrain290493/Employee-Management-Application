package com.amaris.employee_management.exception;

/**
 * Exception thrown when a requested resource is not found in the employee management system.
 * Typically corresponds to HTTP 404 Not Found status.
 */
public class NotFoundException extends EmployeeManagementException {
    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message describing the missing resource
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new NotFoundException with the specified detail message and cause.
     *
     * @param message the detail message describing the missing resource
     * @param cause the underlying cause of the exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}