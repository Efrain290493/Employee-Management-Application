package com.amaris.employee_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain entity representing an employee.
 * Contains core business logic like salary calculation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Long id;
    private String name;
    private Integer age;
    private Double salary;
    private String profileImage;
    private Double annualSalary;
}
