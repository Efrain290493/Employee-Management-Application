package com.amaris.employee_management.repository;

import com.amaris.employee_management.model.EmployeeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Should find employee by ID")
    void findById_ShouldReturnEmployee() {
        // Arrange
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName("John Doe");
        employee.setAge(30);
        employee.setSalary(5000.0);
        employee.setAnnualSalary(60000.0);
        employee.setLastUpdated(LocalDateTime.now());

        // Persist and flush to ensure the entity is in the database
        entityManager.persistAndFlush(employee);

        // Act
        Optional<EmployeeEntity> found = employeeRepository.findById(employee.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
        assertEquals(30, found.get().getAge());
    }

    @Test
    @DisplayName("Should return empty when ID not found")
    void findById_WhenIdNotFound_ShouldReturnEmpty() {
        // Act
        Optional<EmployeeEntity> found = employeeRepository.findById(999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all employees")
    void findAll_ShouldReturnAllEmployees() {
        // Arrange
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setName("John Doe");
        employee1.setAge(30);
        employee1.setSalary(5000.0);
        employee1.setLastUpdated(LocalDateTime.now());

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setName("Jane Smith");
        employee2.setAge(35);
        employee2.setSalary(6000.0);
        employee2.setLastUpdated(LocalDateTime.now());

        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        // Act
        List<EmployeeEntity> employees = employeeRepository.findAll();

        // Assert
        assertEquals(2, employees.size());
    }

    @Test
    @DisplayName("Should find employees updated after timestamp")
    void findByLastUpdatedAfter_ShouldReturnUpdatedEmployees() {
        // Arrange
        LocalDateTime pastTime = LocalDateTime.now().minusDays(5);
        LocalDateTime recentTime = LocalDateTime.now().minusHours(1);

        EmployeeEntity oldEmployee = new EmployeeEntity();
        oldEmployee.setName("Old Employee");
        oldEmployee.setAge(40);
        oldEmployee.setSalary(4000.0);
        oldEmployee.setLastUpdated(pastTime);

        EmployeeEntity recentEmployee = new EmployeeEntity();
        recentEmployee.setName("Recent Employee");
        recentEmployee.setAge(25);
        recentEmployee.setSalary(3000.0);
        recentEmployee.setLastUpdated(LocalDateTime.now());

        entityManager.persist(oldEmployee);
        entityManager.persist(recentEmployee);
        entityManager.flush();

        // Act
        List<EmployeeEntity> updatedEmployees = employeeRepository.findByLastUpdatedAfter(recentTime);

        // Assert
        assertEquals(1, updatedEmployees.size());
        assertEquals("Recent Employee", updatedEmployees.get(0).getName());
    }

    @Test
    @DisplayName("Should find employees by name case insensitive")
    void findByNameContainingIgnoreCase_ShouldReturnMatchingEmployees() {
        // Arrange
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setName("John Doe");
        employee1.setAge(30);
        employee1.setSalary(5000.0);

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setName("Jane Smith");
        employee2.setAge(35);
        employee2.setSalary(6000.0);

        EmployeeEntity employee3 = new EmployeeEntity();
        employee3.setName("John Smith");
        employee3.setAge(40);
        employee3.setSalary(7000.0);

        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.persist(employee3);
        entityManager.flush();

        // Act - case insensitive search for "john"
        List<EmployeeEntity> johnEmployees = employeeRepository.findByNameContainingIgnoreCase("john");

        // Act - case insensitive search for "SMITH"
        List<EmployeeEntity> smithEmployees = employeeRepository.findByNameContainingIgnoreCase("SMITH");

        // Assert
        assertEquals(2, johnEmployees.size());
        assertEquals(2, smithEmployees.size());
    }

    @Test
    @DisplayName("Should save employee")
    void save_ShouldPersistEmployee() {
        // Arrange
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName("New Employee");
        employee.setAge(28);
        employee.setSalary(4500.0);
        employee.setAnnualSalary(54000.0);
        employee.setLastUpdated(LocalDateTime.now());

        // Act
        EmployeeEntity saved = employeeRepository.save(employee);

        // Assert
        assertNotNull(saved.getId());

        // Verify it's in the database
        EmployeeEntity found = entityManager.find(EmployeeEntity.class, saved.getId());
        assertNotNull(found);
        assertEquals("New Employee", found.getName());
    }

    @Test
    @DisplayName("Should update employee")
    void save_ShouldUpdateExistingEmployee() {
        // Arrange
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName("Original Name");
        employee.setAge(30);
        employee.setSalary(5000.0);
        entityManager.persistAndFlush(employee);

        // Act - update the employee
        employee.setName("Updated Name");
        employee.setSalary(5500.0);
        employeeRepository.save(employee);

        // Assert
        EmployeeEntity updated = entityManager.find(EmployeeEntity.class, employee.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals(5500.0, updated.getSalary());
    }

    @Test
    @DisplayName("Should save multiple employees")
    void saveAll_ShouldPersistMultipleEmployees() {
        // Arrange
        EmployeeEntity employee1 = new EmployeeEntity();
        employee1.setName("First Employee");
        employee1.setAge(30);
        employee1.setSalary(5000.0);

        EmployeeEntity employee2 = new EmployeeEntity();
        employee2.setName("Second Employee");
        employee2.setAge(35);
        employee2.setSalary(6000.0);

        List<EmployeeEntity> employees = List.of(employee1, employee2);

        // Act
        List<EmployeeEntity> saved = employeeRepository.saveAll(employees);

        // Assert
        assertEquals(2, saved.size());
        assertNotNull(saved.get(0).getId());
        assertNotNull(saved.get(1).getId());

        // Verify they're in the database
        List<EmployeeEntity> found = employeeRepository.findAll();
        assertEquals(2, found.size());
    }

    @Test
    @DisplayName("Should return empty list when no updated employees found")
    void findByLastUpdatedAfter_WhenNoMatches_ShouldReturnEmptyList() {
        // Arrange
        LocalDateTime pastTime = LocalDateTime.now().minusDays(5);

        EmployeeEntity oldEmployee = new EmployeeEntity();
        oldEmployee.setName("Old Employee");
        oldEmployee.setAge(40);
        oldEmployee.setSalary(4000.0);
        oldEmployee.setLastUpdated(pastTime);

        entityManager.persist(oldEmployee);
        entityManager.flush();

        // Act - search for updates after now (which won't exist)
        List<EmployeeEntity> updatedEmployees = employeeRepository.findByLastUpdatedAfter(LocalDateTime.now());

        // Assert
        assertTrue(updatedEmployees.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no name matches found")
    void findByNameContainingIgnoreCase_WhenNoMatches_ShouldReturnEmptyList() {
        // Arrange
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName("John Doe");
        employee.setAge(30);
        employee.setSalary(5000.0);

        entityManager.persist(employee);
        entityManager.flush();

        // Act
        List<EmployeeEntity> nonExistentEmployees = employeeRepository.findByNameContainingIgnoreCase("NonExistent");

        // Assert
        assertTrue(nonExistentEmployees.isEmpty());
    }
}