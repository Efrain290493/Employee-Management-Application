    package com.amaris.employee_management.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO {
    private String status;
    private Object data;
    private String message;

    public List<EmployeeDTO> getData() {
        if (data instanceof List) {
            return (List<EmployeeDTO>) data;
        }
        return null;
    }

    public EmployeeDTO getEmployeeData() {
        if (data instanceof EmployeeDTO) {
            return (EmployeeDTO) data;
        }
        return null;
    }
}