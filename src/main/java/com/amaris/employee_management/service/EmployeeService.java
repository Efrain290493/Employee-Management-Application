package com.amaris.employee_management.service;

import com.amaris.employee_management.model.Employee;

import java.util.List;

/**
 * Service interface for employee-related operations.
 *
 * Defines the contract for retrieving employee information
 * and performing salary calculations.
 *
 * @author Efrain Lopez
 * @version 1.0
 * @since 31-03-2025
 */
public interface EmployeeService {
    /**
     * Retrieves a list of all employees.
     *
     * @return List of all employees
     */
    List<Employee> findAllEmployees();

    /**
     * Finds an employee by their unique identifier.
     *
     * @param id Unique identifier of the employee
     * @return Employee details
     */
    Employee findEmployeeById(String id);

    /**
     * Calculates the annual salary for a specific employee.
     *
     * @param id Unique identifier of the employee
     * @return Annual salary of the employee
     */
    Double calculateAnnualSalary(String id);
}
