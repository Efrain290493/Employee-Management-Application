package com.amaris.employee_management.mapper;

import com.amaris.employee_management.client.dto.EmployeeDTO;
import com.amaris.employee_management.model.Employee;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting EmployeeDTO to Employee domain model.
 *
 * Provides method to transform external data transfer objects
 * into internal domain representation.
 *
 * @author Efrain Lopez
 * @version 1.0
 * @since 31-03-2025
 */
@Component
public class EmployeeMapper {
    /**
     * Converts EmployeeDTO to Employee domain object.
     *
     * @param dto Data Transfer Object containing employee information
     * @return Mapped Employee domain object, or null if input is null
     */
    public Employee toEmployee(EmployeeDTO dto) {
        if (dto == null) {
            return null;
        }

        return Employee.builder()
                .id(dto.getId())
                .name(dto.getName())
                .age(dto.getAge())
                .salary(dto.getSalary())
                .profileImage(dto.getProfileImage())
                .build();
    }
}