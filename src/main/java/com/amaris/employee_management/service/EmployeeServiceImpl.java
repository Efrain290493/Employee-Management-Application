package com.amaris.employee_management.service;

import com.amaris.employee_management.client.EmployeeFeignClient;
import com.amaris.employee_management.client.dto.EmployeeDTO;
import com.amaris.employee_management.client.dto.ResponseDTO;
import com.amaris.employee_management.config.FeignRateLimitInterceptor;
import com.amaris.employee_management.exception.FeignErrorDecoder.RateLimitExceededException;
import com.amaris.employee_management.exception.FeignErrorDecoder.ResourceNotFoundException;
import com.amaris.employee_management.mapper.EmployeeMapper;
import com.amaris.employee_management.model.EmployeeEntity;
import com.amaris.employee_management.repository.EmployeeRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the EmployeeService interface.
 * Provides employee data access with caching and fallback mechanisms.
 *
 * This service tries to fetch data from external API first,
 * then falls back to local database if needed, and uses caching for performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeFeignClient feignClient;
    private final EmployeeMapper employeeMapper;
    private final EmployeeRepository employeeRepository;
    private final FeignRateLimitInterceptor rateLimiter;

    @Override
    @Cacheable(value = "employees", key = "'all'", unless = "#result.isEmpty()")
    public List<EmployeeEntity> findAllEmployees() {
        try {
            // Try to get data from external service
            ResponseDTO<List<EmployeeDTO>> response = feignClient.getAllEmployees();

            if (response == null || response.getData() == null) {
                log.warn("Null or empty response when retrieving all employees");
                return fallbackToDatabase();
            }

            List<EmployeeEntity> employeeEntities = response.getData().stream()
                    .map(employeeMapper::toEmployee)
                    .map(this::addAnnualSalary)
                    .map(this::addLastUpdated)  // Add update timestamp
                    .collect(Collectors.toList());

            // Save to local database for future fallbacks
            employeeRepository.saveAll(employeeEntities);
            log.info("Saved {} employees to local database", employeeEntities.size());

            return employeeEntities;
        } catch (RateLimitExceededException e) {
            log.warn("Rate limit exceeded when retrieving all employees", e);
            return fallbackToDatabase();
        } catch (Exception e) {
            log.error("Error retrieving all employees from external service", e);
            // If there's an error, use local database as fallback
            return fallbackToDatabase();
        } finally {
            // Make sure to release the rate limiter permit
            rateLimiter.releasePermit();
        }
    }

    @Override
    @Cacheable(value = "employees", key = "#id", unless = "#result == null")
    public EmployeeEntity findEmployeeById(String id) {
        try {
            // Try to get data from external service
            ResponseDTO<EmployeeDTO> response = feignClient.getEmployeeById(id);

            if (response == null || response.getData() == null) {
                log.warn("Employee not found with ID: {}", id);
                return fallbackToDatabase(Long.parseLong(id));
            }

            EmployeeEntity employeeEntity = employeeMapper.toEmployee(response.getData());
            employeeEntity = addAnnualSalary(employeeEntity);
            employeeEntity = addLastUpdated(employeeEntity);  // Add update timestamp

            // Save to local database for future fallbacks
            employeeRepository.save(employeeEntity);
            log.info("Saved employee with ID: {} to local database", employeeEntity.getId());

            return employeeEntity;
        } catch (ResourceNotFoundException | FeignException.NotFound e) {
            log.warn("Employee not found with ID: {}", id, e);
            return fallbackToDatabase(Long.parseLong(id));
        } catch (RateLimitExceededException e) {
            log.warn("Rate limit exceeded when finding employee with ID: {}", id, e);
            return fallbackToDatabase(Long.parseLong(id));
        } catch (Exception e) {
            log.error("Error finding employee with ID: {}", id, e);
            return fallbackToDatabase(Long.parseLong(id));
        } finally {
            // Make sure to release the rate limiter permit
            rateLimiter.releasePermit();
        }
    }

    @Override
    public Double calculateAnnualSalary(String id) {
        EmployeeEntity employeeEntity = findEmployeeById(id);
        if (employeeEntity == null || employeeEntity.getSalary() == null) {
            throw new ResourceNotFoundException("Could not calculate annual salary for employee with ID: " + id);
        }
        return employeeEntity.getSalary() * 12;
    }

    /**
     * Method to retrieve all employees from local database (fallback).
     * Used when external API is unavailable or returns an error.
     *
     * @return List of employees from local database
     */
    private List<EmployeeEntity> fallbackToDatabase() {
        log.info("Using data from local database for all employees");
        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();

        if (employeeEntities.isEmpty()) {
            log.warn("No employees found in local database");
            return Collections.emptyList();
        }

        return employeeEntities;
    }

    /**
     * Method to retrieve an employee from local database by ID (fallback).
     * Used when external API is unavailable or returns an error.
     *
     * @param id Employee ID to search for
     * @return Employee from local database or null if not found
     */
    private EmployeeEntity fallbackToDatabase(Long id) {
        log.info("Using data from local database for employee with ID: {}", id);
        Optional<EmployeeEntity> employeeOpt = employeeRepository.findById(id);

        if (employeeOpt.isEmpty()) {
            log.warn("No employee found with ID: {} in local database", id);
            return null;
        }

        return employeeOpt.get();
    }

    /**
     * Helper method to calculate and set annual salary.
     *
     * @param employeeEntity Employee object to update
     * @return Updated employee with annual salary set
     */
    private EmployeeEntity addAnnualSalary(EmployeeEntity employeeEntity) {
        if (employeeEntity != null && employeeEntity.getSalary() != null) {
            employeeEntity.setAnnualSalary(employeeEntity.getSalary() * 12);
        }
        return employeeEntity;
    }

    /**
     * Helper method to add the last update timestamp.
     *
     * @param employeeEntity Employee object to update
     * @return Updated employee with lastUpdated timestamp set
     */
    private EmployeeEntity addLastUpdated(EmployeeEntity employeeEntity) {
        if (employeeEntity != null) {
            employeeEntity.setLastUpdated(LocalDateTime.now());
        }
        return employeeEntity;
    }

    /**
     * Scheduled task to clear cache periodically.
     * Runs every day at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @CacheEvict(value = "employees", allEntries = true)
    public void clearCache() {
        log.info("Clearing employee cache");
    }
}