package com.amaris.employee_management.exception;

// Excepción base para errores personalizados
public class EmployeeManagementException extends RuntimeException {
    public EmployeeManagementException(String message) {
        super(message);
    }

    public EmployeeManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
