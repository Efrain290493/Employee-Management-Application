package com.amaris.employee_management.controller;

import com.amaris.employee_management.exception.FeignErrorDecoder.ResourceNotFoundException;
import com.amaris.employee_management.model.EmployeeEntity;
import com.amaris.employee_management.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private EmployeeEntity employee1;
    private EmployeeEntity employee2;
    private List<EmployeeEntity> employeeList;

    @BeforeEach
    void setUp() {
        // Set up test data
        employee1 = new EmployeeEntity();
        employee1.setId(1L);
        employee1.setName("John Doe");
        employee1.setAge(30);
        employee1.setSalary(5000.0);
        employee1.setAnnualSalary(60000.0);
        employee1.setLastUpdated(LocalDateTime.now());

        employee2 = new EmployeeEntity();
        employee2.setId(2L);
        employee2.setName("Jane Smith");
        employee2.setAge(35);
        employee2.setSalary(6000.0);
        employee2.setAnnualSalary(72000.0);
        employee2.setLastUpdated(LocalDateTime.now());

        employeeList = Arrays.asList(employee1, employee2);
    }

    @Test
    @DisplayName("Should return all employees")
    void getAllEmployees_ShouldReturnAllEmployees() {
        // Arrange
        when(employeeService.findAllEmployees()).thenReturn(employeeList);

        // Act
        ResponseEntity<List<EmployeeEntity>> response = employeeController.getAllEmployees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeList, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(employeeService, times(1)).findAllEmployees();
    }

    @Test
    @DisplayName("Should return empty list when no employees found")
    void getAllEmployees_WhenNoEmployeesFound_ShouldReturnEmptyList() {
        // Arrange
        when(employeeService.findAllEmployees()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<EmployeeEntity>> response = employeeController.getAllEmployees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(employeeService, times(1)).findAllEmployees();
    }

    @Test
    @DisplayName("Should return employee by ID")
    void getEmployeeById_ShouldReturnEmployee() {
        // Arrange
        String employeeId = "1";
        when(employeeService.findEmployeeById(employeeId)).thenReturn(employee1);

        // Act
        ResponseEntity<EmployeeEntity> response = employeeController.getEmployeeById(employeeId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employee1, response.getBody());
        verify(employeeService, times(1)).findEmployeeById(employeeId);
    }

    @Test
    @DisplayName("Should handle null employee returned by service")
    void getEmployeeById_WhenEmployeeNotFound_ShouldReturnNull() {
        // Arrange
        String employeeId = "999";
        when(employeeService.findEmployeeById(employeeId)).thenReturn(null);

        // Act
        ResponseEntity<EmployeeEntity> response = employeeController.getEmployeeById(employeeId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(employeeService, times(1)).findEmployeeById(employeeId);
    }

    @Test
    @DisplayName("Should return annual salary for employee")
    void calculateAnnualSalary_ShouldReturnAnnualSalary() {
        // Arrange
        String employeeId = "1";
        double expectedSalary = 60000.0;
        when(employeeService.calculateAnnualSalary(employeeId)).thenReturn(expectedSalary);

        // Act
        ResponseEntity<Double> response = employeeController.calculateAnnualSalary(employeeId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedSalary, response.getBody());
        verify(employeeService, times(1)).calculateAnnualSalary(employeeId);
    }

    @Test
    @DisplayName("Should handle exception from service when calculating annual salary")
    void calculateAnnualSalary_WhenExceptionThrown_ShouldPropagateException() {
        // Arrange
        String employeeId = "999";
        when(employeeService.calculateAnnualSalary(employeeId))
                .thenThrow(new ResourceNotFoundException("Could not calculate annual salary for employee with ID: " + employeeId));

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeController.calculateAnnualSalary(employeeId);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Could not calculate annual salary"));
        verify(employeeService, times(1)).calculateAnnualSalary(employeeId);
    }

    @Test
    @DisplayName("Constructor should properly inject dependencies")
    void constructor_ShouldInjectDependencies() {
        // Arrange
        EmployeeService mockedService = mock(EmployeeService.class);

        // Act
        EmployeeController controller = new EmployeeController(mockedService);

        // Assert - verify that controller is created without exceptions
        assertNotNull(controller);
    }
}