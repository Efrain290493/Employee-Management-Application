package com.amaris.employee_management.exception;

/**
        * Exception thrown when a bad request is made to the employee management system.
        * Typically corresponds to HTTP 400 Bad Request status.
        */
public class BadRequestException extends EmployeeManagementException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
