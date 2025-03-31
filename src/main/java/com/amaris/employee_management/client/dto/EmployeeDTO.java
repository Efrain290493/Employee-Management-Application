package com.amaris.employee_management.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;

    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_age")
    private Integer age;

    @JsonProperty("employee_salary")
    private Double salary;

    @JsonProperty("profile_image")
    private String profileImage;
}
