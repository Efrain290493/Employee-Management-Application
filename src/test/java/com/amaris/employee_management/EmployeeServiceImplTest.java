package com.amaris.employee_management;

import com.amaris.employee_management.client.EmployeeFeignClient;
import com.amaris.employee_management.client.dto.EmployeeDTO;
import com.amaris.employee_management.client.dto.ResponseDTO;
import com.amaris.employee_management.config.FeignRateLimitInterceptor;
import com.amaris.employee_management.exception.FeignErrorDecoder.RateLimitExceededException;
import com.amaris.employee_management.exception.FeignErrorDecoder.ResourceNotFoundException;
import com.amaris.employee_management.mapper.EmployeeMapper;
import com.amaris.employee_management.model.EmployeeEntity;
import com.amaris.employee_management.repository.EmployeeRepository;
import com.amaris.employee_management.service.EmployeeServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeFeignClient feignClient;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private FeignRateLimitInterceptor rateLimiter;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeDTO employeeDTO1;
    private EmployeeDTO employeeDTO2;
    private EmployeeEntity employeeEntity1;
    private EmployeeEntity employeeEntity2;
    private ResponseDTO<List<EmployeeDTO>> listResponseDTO;
    private ResponseDTO<EmployeeDTO> singleResponseDTO;

    @BeforeEach
    void setUp() {
        // Set up test DTOs
        employeeDTO1 = new EmployeeDTO();
        employeeDTO1.setId(1L);
        employeeDTO1.setName("John Doe");
        employeeDTO1.setAge(30);
        employeeDTO1.setSalary(5000.0);

        employeeDTO2 = new EmployeeDTO();
        employeeDTO2.setId(2L);
        employeeDTO2.setName("Jane Smith");
        employeeDTO2.setAge(35);
        employeeDTO2.setSalary(6000.0);

        // Set up test Entities
        employeeEntity1 = new EmployeeEntity();
        employeeEntity1.setId(1L);
        employeeEntity1.setName("John Doe");
        employeeEntity1.setAge(30);
        employeeEntity1.setSalary(5000.0);
        employeeEntity1.setLastUpdated(LocalDateTime.now());

        employeeEntity2 = new EmployeeEntity();
        employeeEntity2.setId(2L);
        employeeEntity2.setName("Jane Smith");
        employeeEntity2.setAge(35);
        employeeEntity2.setSalary(6000.0);
        employeeEntity2.setLastUpdated(LocalDateTime.now());

        // Set up response DTOs
        listResponseDTO = new ResponseDTO<>();
        listResponseDTO.setStatus("success");
        listResponseDTO.setData(Arrays.asList(employeeDTO1, employeeDTO2));

        singleResponseDTO = new ResponseDTO<>();
        singleResponseDTO.setStatus("success");
        singleResponseDTO.setData(employeeDTO1);
    }

    @Test
    @DisplayName("findAllEmployees should return employees from API when successful")
    void findAllEmployees_WhenApiSuccessful_ShouldReturnEmployees() {
        // Arrange
        when(feignClient.getAllEmployees()).thenReturn(listResponseDTO);
        when(employeeMapper.toEmployee(employeeDTO1)).thenReturn(employeeEntity1);
        when(employeeMapper.toEmployee(employeeDTO2)).thenReturn(employeeEntity2);
        when(employeeRepository.saveAll(anyList())).thenReturn(Arrays.asList(employeeEntity1, employeeEntity2));

        // Act
        List<EmployeeEntity> result = employeeService.findAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(feignClient, times(1)).getAllEmployees();
        verify(employeeMapper, times(2)).toEmployee(any(EmployeeDTO.class));
        verify(employeeRepository, times(1)).saveAll(anyList());
        verify(rateLimiter, times(1)).releasePermit();

        // Verify that annual salary was calculated
        assertTrue(result.stream().allMatch(e -> e.getAnnualSalary() != null));
        // Verify that lastUpdated was set
        assertTrue(result.stream().allMatch(e -> e.getLastUpdated() != null));
    }

    @Test
    @DisplayName("findAllEmployees should fall back to database when API returns null")
    void findAllEmployees_WhenApiReturnsNull_ShouldFallbackToDatabase() {
        // Arrange
        when(feignClient.getAllEmployees()).thenReturn(null);
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeEntity1, employeeEntity2));

        // Act
        List<EmployeeEntity> result = employeeService.findAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(feignClient, times(1)).getAllEmployees();
        verify(employeeRepository, times(1)).findAll();
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findAllEmployees should fall back to database when API data is null")
    void findAllEmployees_WhenApiDataIsNull_ShouldFallbackToDatabase() {
        // Arrange
        ResponseDTO<List<EmployeeDTO>> nullDataResponse = new ResponseDTO<>();
        nullDataResponse.setStatus("success");
        nullDataResponse.setData(null);

        when(feignClient.getAllEmployees()).thenReturn(nullDataResponse);
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeEntity1, employeeEntity2));

        // Act
        List<EmployeeEntity> result = employeeService.findAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(feignClient, times(1)).getAllEmployees();
        verify(employeeRepository, times(1)).findAll();
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findAllEmployees should fall back to database when rate limit exceeded")
    void findAllEmployees_WhenRateLimitExceeded_ShouldFallbackToDatabase() {
        // Arrange
        when(feignClient.getAllEmployees()).thenThrow(new RateLimitExceededException("Rate limit exceeded"));
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeEntity1, employeeEntity2));

        // Act
        List<EmployeeEntity> result = employeeService.findAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(feignClient, times(1)).getAllEmployees();
        verify(employeeRepository, times(1)).findAll();
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findAllEmployees should fall back to database when generic exception occurs")
    void findAllEmployees_WhenGenericException_ShouldFallbackToDatabase() {
        // Arrange
        when(feignClient.getAllEmployees()).thenThrow(new RuntimeException("Generic error"));
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employeeEntity1, employeeEntity2));

        // Act
        List<EmployeeEntity> result = employeeService.findAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(feignClient, times(1)).getAllEmployees();
        verify(employeeRepository, times(1)).findAll();
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findAllEmployees should return empty list when database fallback is empty")
    void findAllEmployees_WhenDatabaseFallbackEmpty_ShouldReturnEmptyList() {
        // Arrange
        when(feignClient.getAllEmployees()).thenThrow(new RuntimeException("Generic error"));
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeEntity> result = employeeService.findAllEmployees();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(feignClient, times(1)).getAllEmployees();
        verify(employeeRepository, times(1)).findAll();
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should return employee from API when successful")
    void findEmployeeById_WhenApiSuccessful_ShouldReturnEmployee() {
        // Arrange
        String id = "1";
        when(feignClient.getEmployeeById(id)).thenReturn(singleResponseDTO);
        when(employeeMapper.toEmployee(employeeDTO1)).thenReturn(employeeEntity1);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(employeeEntity1);

        // Act
        EmployeeEntity result = employeeService.findEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals(30, result.getAge());
        assertEquals(5000.0, result.getSalary());
        assertNotNull(result.getAnnualSalary());
        assertEquals(60000.0, result.getAnnualSalary()); // 5000 * 12
        assertNotNull(result.getLastUpdated());
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeMapper, times(1)).toEmployee(any(EmployeeDTO.class));
        verify(employeeRepository, times(1)).save(any(EmployeeEntity.class));
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should fall back to database when API returns null")
    void findEmployeeById_WhenApiReturnsNull_ShouldFallbackToDatabase() {
        // Arrange
        String id = "1";
        when(feignClient.getEmployeeById(id)).thenReturn(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeEntity1));

        // Act
        EmployeeEntity result = employeeService.findEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeRepository, times(1)).findById(1L);
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should fall back to database when API data is null")
    void findEmployeeById_WhenApiDataIsNull_ShouldFallbackToDatabase() {
        // Arrange
        String id = "1";
        ResponseDTO<EmployeeDTO> nullDataResponse = new ResponseDTO<>();
        nullDataResponse.setStatus("success");
        nullDataResponse.setData(null);

        when(feignClient.getEmployeeById(id)).thenReturn(nullDataResponse);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeEntity1));

        // Act
        EmployeeEntity result = employeeService.findEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeRepository, times(1)).findById(1L);
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should fall back to database when resource not found")
    void findEmployeeById_WhenResourceNotFound_ShouldFallbackToDatabase() {
        // Arrange
        String id = "1";
        when(feignClient.getEmployeeById(id)).thenThrow(new ResourceNotFoundException("Employee not found"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeEntity1));

        // Act
        EmployeeEntity result = employeeService.findEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeRepository, times(1)).findById(1L);
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should fall back to database when FeignException.NotFound occurs")
    void findEmployeeById_WhenFeignNotFound_ShouldFallbackToDatabase() {
        // Arrange
        String id = "1";
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(feignClient.getEmployeeById(id)).thenThrow(notFoundException);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeEntity1));

        // Act
        EmployeeEntity result = employeeService.findEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeRepository, times(1)).findById(1L);
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should fall back to database when rate limit exceeded")
    void findEmployeeById_WhenRateLimitExceeded_ShouldFallbackToDatabase() {
        // Arrange
        String id = "1";
        when(feignClient.getEmployeeById(id)).thenThrow(new RateLimitExceededException("Rate limit exceeded"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeEntity1));

        // Act
        EmployeeEntity result = employeeService.findEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeRepository, times(1)).findById(1L);
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should fall back to database when generic exception occurs")
    void findEmployeeById_WhenGenericException_ShouldFallbackToDatabase() {
        // Arrange
        String id = "1";
        when(feignClient.getEmployeeById(id)).thenThrow(new RuntimeException("Generic error"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employeeEntity1));

        // Act
        EmployeeEntity result = employeeService.findEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeRepository, times(1)).findById(1L);
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("findEmployeeById should throw ResourceNotFoundException when database fallback is empty")
    void findEmployeeById_WhenDatabaseFallbackEmpty_ShouldThrowException() {
        // Arrange
        String id = "1";
        when(feignClient.getEmployeeById(id)).thenThrow(new RuntimeException("Generic error"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.findEmployeeById(id);
        });

        // Verify exception message
        assertTrue(exception.getMessage().contains("Employee not found with ID: " + id));

        // Verify method calls
        verify(feignClient, times(1)).getEmployeeById(id);
        verify(employeeRepository, times(1)).findById(1L);
        verify(rateLimiter, times(1)).releasePermit();
    }

    @Test
    @DisplayName("calculateAnnualSalary should return correct annual salary")
    void calculateAnnualSalary_ShouldReturnCorrectAnnualSalary() {
        // Arrange
        String id = "1";
        employeeEntity1.setSalary(5000.0);

        // Configurar el comportamiento del repositorio y el cliente para que findEmployeeById retorne nuestro empleado
        when(feignClient.getEmployeeById(id)).thenReturn(singleResponseDTO);
        when(employeeMapper.toEmployee(employeeDTO1)).thenReturn(employeeEntity1);

        // Act
        Double result = employeeService.calculateAnnualSalary(id);

        // Assert
        assertEquals(60000.0, result); // 5000 * 12
    }

    @Test
    @DisplayName("calculateAnnualSalary should throw ResourceNotFoundException when employee not found")
    void calculateAnnualSalary_WhenEmployeeNotFound_ShouldThrowException() {
        // Arrange
        String id = "999";

        // Configurar el comportamiento del repositorio
        when(employeeRepository.findById(Long.parseLong(id))).thenReturn(Optional.empty());

        // Configurar que el cliente Feign lance una excepción para forzar el fallback
        when(feignClient.getEmployeeById(id)).thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.calculateAnnualSalary(id);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Employee not found with ID: " + id));
    }

    @Test
    @DisplayName("calculateAnnualSalary should throw ResourceNotFoundException when salary is null")
    void calculateAnnualSalary_WhenSalaryNull_ShouldThrowException() {
        // Arrange
        String id = "1";
        employeeEntity1.setSalary(null);

        // Configurar el comportamiento del repositorio y el cliente Feign
        ResponseDTO<EmployeeDTO> response = new ResponseDTO<>();
        response.setStatus("success");
        response.setData(employeeDTO1);

        when(feignClient.getEmployeeById(id)).thenReturn(response);
        when(employeeMapper.toEmployee(employeeDTO1)).thenReturn(employeeEntity1);

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.calculateAnnualSalary(id);
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Could not calculate annual salary"));
    }

    @Test
    @DisplayName("clearCache should execute without errors")
    void clearCache_ShouldExecuteWithoutErrors() {
        // Act
        employeeService.clearCache();

        // Assert - verify no exception is thrown
        // This is mainly to ensure method coverage
    }

    @Test
    @DisplayName("addAnnualSalary should calculate correct annual salary")
    void addAnnualSalary_ShouldCalculateCorrectAnnualSalary() {
        // Arrange
        EmployeeEntity employee = new EmployeeEntity();
        employee.setSalary(5000.0);

        // Use reflection to access private method
        EmployeeEntity result = (EmployeeEntity) ReflectionTestUtils.invokeMethod(
                employeeService,
                "addAnnualSalary",
                employee
        );

        // Assert
        assertNotNull(result);
        assertEquals(60000.0, result.getAnnualSalary()); // 5000 * 12
    }

    @Test
    @DisplayName("addAnnualSalary should handle null employee")
    void addAnnualSalary_WithNullEmployee_ShouldReturnNull() {
        // Use reflection to access private method
        EmployeeEntity result = (EmployeeEntity) ReflectionTestUtils.invokeMethod(
                employeeService,
                "addAnnualSalary",
                (EmployeeEntity) null
        );

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("addAnnualSalary should handle null salary")
    void addAnnualSalary_WithNullSalary_ShouldNotSetAnnualSalary() {
        // Arrange
        EmployeeEntity employee = new EmployeeEntity();
        employee.setSalary(null);

        // Use reflection to access private method
        EmployeeEntity result = (EmployeeEntity) ReflectionTestUtils.invokeMethod(
                employeeService,
                "addAnnualSalary",
                employee
        );

        // Assert
        assertNotNull(result);
        assertNull(result.getAnnualSalary());
    }

    @Test
    @DisplayName("addLastUpdated should set current timestamp")
    void addLastUpdated_ShouldSetTimestamp() {
        // Arrange
        EmployeeEntity employee = new EmployeeEntity();
        LocalDateTime beforeTest = LocalDateTime.now();

        // Use reflection to access private method
        EmployeeEntity result = (EmployeeEntity) ReflectionTestUtils.invokeMethod(
                employeeService,
                "addLastUpdated",
                employee
        );

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLastUpdated());
        // Verify timestamp is after or equal to the time before test
        assertTrue(result.getLastUpdated().isEqual(beforeTest) ||
                result.getLastUpdated().isAfter(beforeTest));
    }

    @Test
    @DisplayName("addLastUpdated should handle null employee")
    void addLastUpdated_WithNullEmployee_ShouldReturnNull() {
        // Use reflection to access private method
        EmployeeEntity result = (EmployeeEntity) ReflectionTestUtils.invokeMethod(
                employeeService,
                "addLastUpdated",
                (EmployeeEntity) null
        );

        // Assert
        assertNull(result);
    }
}