package com.amaris.employee_management.service;

import com.amaris.employee_management.client.EmployeeFeignClient;
import com.amaris.employee_management.mapper.EmployeeMapper;
import com.amaris.employee_management.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EmployeeService that interacts with an external
 * employee data source using Feign Client.
 *
 * Provides methods for retrieving employee information,
 * with caching capabilities to improve performance.
 *
 * @author Efrain Lopez
 * @version 1.0
 * @since 31-03-2025
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    /** Feign client for external employee data retrieval */
    private final EmployeeFeignClient feignClient;

    /** Mapper to convert DTO to Employee domain model */
    private final EmployeeMapper employeeMapper;

    /**
     * Retrieves all employees from the external source.
     *
     * Cacheable method to improve performance by storing
     * previously fetched employee lists.
     *
     * @return List of all employees with annual salary calculated
     */
    @Override
    @Cacheable("employees")
    public List<Employee> findAllEmployees() {
        var response = feignClient.getAllEmployees();
        return response.getData().stream()
                .map(employeeMapper::toEmployee)
                .map(this::addAnnualSalary)
                .collect(Collectors.toList());
    }

    /**
     * Finds a specific employee by their ID.
     *
     * Cached method to store and retrieve employee details
     * based on their unique identifier.
     *
     * @param id Unique identifier of the employee
     * @return Employee details with annual salary
     */
    @Override
    @Cacheable(value = "employees", key = "#id")
    public Employee findEmployeeById(String id) {
        var response = feignClient.getEmployeeById(id);
        var employee = employeeMapper.toEmployee(response.getEmployeeData());
        return addAnnualSalary(employee);
    }

    /**
     * Calculates the annual salary for a specific employee.
     *
     * @param id Unique identifier of the employee
     * @return Annual salary calculated by multiplying monthly salary by 12
     */
    @Override
    public Double calculateAnnualSalary(String id) {
        var employee = findEmployeeById(id);
        return employee.getSalary() * 12;
    }

    /**
     * Adds annual salary to an employee object.
     *
     * Calculates and sets the annual salary based on the monthly salary.
     *
     * @param employee Employee object to update
     * @return Employee with annual salary added
     */
    private Employee addAnnualSalary(Employee employee) {
        if (employee != null && employee.getSalary() != null) {
            employee.setAnnualSalary(employee.getSalary() * 12);
        }
        return employee;
    }
}