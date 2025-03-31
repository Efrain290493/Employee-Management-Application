package com.amaris.employee_management.repository;

import com.amaris.employee_management.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for accessing employee data in the local database.
 * Provides CRUD operations and custom query methods for Employee entities.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    /**
     * Finds all employees updated after a specific timestamp.
     * Useful for verifying data freshness and implementing sync strategies.
     *
     * @param timestamp The reference time to filter updated records
     * @return List of employees updated after the specified timestamp
     */
    List<EmployeeEntity> findByLastUpdatedAfter(LocalDateTime timestamp);

    /**
     * Finds employees by name using a case-insensitive partial match.
     * Allows for flexible name-based searches without exact matches.
     *
     * @param name The partial name to search for
     * @return List of employees whose names contain the specified string
     */
    List<EmployeeEntity> findByNameContainingIgnoreCase(String name);
}