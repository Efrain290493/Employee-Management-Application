package com.amaris.employee_management.client;

import com.amaris.employee_management.client.dto.ResponseDTO;
import com.amaris.employee_management.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for external employee data retrieval.
 *
 * Defines REST API methods to fetch employee information
 * from an external service using declarative HTTP clients.
 *
 * @author Efrain Lopez
 * @version 1.0
 * @since 31-03-2025
 */
@FeignClient(
        name = "employeeClient",
        url = "${api.external.base-url}",
        configuration = FeignClientConfig.class
)
public interface EmployeeFeignClient {
    /**
     * Retrieves all employees from the external API.
     *
     * @return ResponseDTO containing list of employees
     */
    @GetMapping("/${api.external.employees-endpoint}")
    ResponseDTO getAllEmployees();

    /**
     * Retrieves a specific employee by their ID from the external API.
     *
     * @param id Unique identifier of the employee
     * @return ResponseDTO containing employee details
     */
    @GetMapping("/${api.external.employee-by-id-endpoint}")
    ResponseDTO getEmployeeById(@PathVariable String id);
}