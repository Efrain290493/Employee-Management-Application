package com.amaris.employee_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for Employee Management System.
 * Configured as a SpringBootServletInitializer to support WAR deployment.
 */
@SpringBootApplication
@EnableCaching
@EnableFeignClients
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}