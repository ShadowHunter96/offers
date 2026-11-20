package com.example.demo.service;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 9:53
 */

import com.example.demo.dto.EmployeeDto;
import com.example.demo.entity.Employee;

import java.time.Instant;

public final class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static Employee toEntity(EmployeeDto dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setRole(dto.getRole());
        employee.setHourlyRate(dto.getHourlyRate());
        employee.setActive(dto.getActive() != null ? dto.getActive() : true);
        employee.setCreatedAt(Instant.now());
        employee.setUpdatedAt(Instant.now());
        return employee;
    }

    public static EmployeeDto toDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setRole(employee.getRole());
        dto.setHourlyRate(employee.getHourlyRate());
        dto.setActive(employee.isActive());
        return dto;
    }
}

