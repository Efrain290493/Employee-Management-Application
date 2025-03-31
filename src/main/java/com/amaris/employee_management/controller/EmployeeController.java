package com.amaris.employee_management.controller;

import com.amaris.employee_management.model.Employee;
import com.amaris.employee_management.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing employee-related operations.
 *
 * This controller provides endpoints to retrieve employee information,
 * including listing all employees, fetching a specific employee by ID,
 * and calculating annual salary.
 *
 * @author [Your Name]
 * @version 1.0
 * @since [Current Date]
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    /** Service for handling employee-related business logic */
    private final EmployeeService employeeService;

    /**
     * Constructor for dependency injection of EmployeeService.
     *
     * @param employeeService Service to handle employee operations
     */
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Retrieves a list of all employees.
     *
     * @return ResponseEntity containing a list of all employees
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.findAllEmployees());
    }

    /**
     * Retrieves a specific employee by their ID.
     *
     * @param id Unique identifier of the employee
     * @return ResponseEntity containing the employee details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.findEmployeeById(id));
    }

    /**
     * Calculates and retrieves the annual salary for a specific employee.
     *
     * @param id Unique identifier of the employee
     * @return ResponseEntity containing the employee's annual salary
     */
    @GetMapping("/{id}/annual-salary")
    public ResponseEntity<Double> calculateAnnualSalary(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.calculateAnnualSalary(id));
    }
}
