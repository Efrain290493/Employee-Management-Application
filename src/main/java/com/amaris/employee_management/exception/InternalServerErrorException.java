package com.amaris.employee_management.exception;

/**
 * Exception thrown when an internal server error occurs in the employee management system.
 * Typically corresponds to HTTP 500 Internal Server Error status.
 */
public class InternalServerErrorException extends EmployeeManagementException {
    /**
     * Constructs a new InternalServerErrorException with the specified detail message.
     *
     * @param message the detail message describing the internal server error
     */
    public InternalServerErrorException(String message) {
        super(message);
    }

    /**
     * Constructs a new InternalServerErrorException with the specified detail message and cause.
     *
     * @param message the detail message describing the internal server error
     * @param cause the underlying cause of the exception
     */
    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
