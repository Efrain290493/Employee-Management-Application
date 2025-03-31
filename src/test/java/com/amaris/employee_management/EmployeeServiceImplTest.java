package com.amaris.employee_management;

import com.amaris.employee_management.client.EmployeeFeignClient;
import com.amaris.employee_management.client.dto.EmployeeDTO;
import com.amaris.employee_management.client.dto.ResponseDTO;
import com.amaris.employee_management.mapper.EmployeeMapper;
import com.amaris.employee_management.model.Employee;
import com.amaris.employee_management.service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeFeignClient feignClient;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeDTO employeeDTO;
    private Employee employee;
    private ResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        employeeDTO = new EmployeeDTO(1L, "John Doe", 30, 5000.0, "");
        employee = Employee.builder()
                .id(1L)
                .name("John Doe")
                .age(30)
                .salary(5000.0)
                .profileImage("")
                .build();

        responseDTO = new ResponseDTO();
        responseDTO.setStatus("success");
        responseDTO.setData(employeeDTO);
    }

    @Test
    void calculateAnnualSalary_ShouldReturnSalaryMultipliedBy12() {
        // Given
        String employeeId = "1";
        when(feignClient.getEmployeeById(employeeId)).thenReturn(responseDTO);
        when(employeeMapper.toEmployee(employeeDTO)).thenReturn(employee);

        // When
        Double annualSalary = employeeService.calculateAnnualSalary(employeeId);

        // Then
        assertEquals(60000.0, annualSalary);
    }

    @Test
    void findEmployeeById_ShouldReturnEmployeeWithAnnualSalary() {
        // Given
        String employeeId = "1";
        when(feignClient.getEmployeeById(employeeId)).thenReturn(responseDTO);
        when(employeeMapper.toEmployee(employeeDTO)).thenReturn(employee);

        // When
        Employee result = employeeService.findEmployeeById(employeeId);

        // Then
        assertEquals(5000.0, result.getSalary());
        assertEquals(60000.0, result.getAnnualSalary());
    }
}