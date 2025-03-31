package com.amaris.employee_management;

import com.amaris.employee_management.client.dto.EmployeeDTO;
import com.amaris.employee_management.mapper.EmployeeMapper;
import com.amaris.employee_management.model.EmployeeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private final EmployeeMapper mapper = new EmployeeMapper();

    @Test
    @DisplayName("Should map DTO to Entity")
    void toEmployee_ShouldMapDtoToEntity() {
        // Arrange
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setAge(30);
        dto.setSalary(5000.0);
        dto.setProfileImage("image.jpg");

        // Act
        EmployeeEntity entity = mapper.toEmployee(dto);

        // Assert
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("John Doe", entity.getName());
        assertEquals(30, entity.getAge());
        assertEquals(5000.0, entity.getSalary());
        assertEquals("image.jpg", entity.getProfileImage());
        assertNull(entity.getAnnualSalary()); // Should not be mapped automatically
        assertNull(entity.getLastUpdated()); // Should not be mapped automatically
    }

    @Test
    @DisplayName("Should handle null DTO")
    void toEmployee_WithNullDto_ShouldReturnNull() {
        // Act
        EmployeeEntity entity = mapper.toEmployee(null);

        // Assert
        assertNull(entity);
    }
}