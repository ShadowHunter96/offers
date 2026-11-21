package com.example.demo.service;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 10:06
 */


import com.example.demo.dto.EmployeeDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    EmployeeDto create(EmployeeDto dto);

    EmployeeDto update(UUID employeeId, EmployeeDto dto);

    EmployeeDto getById(UUID employeeId);
    EmployeeDto getByFirstName(String firstName);
    EmployeeDto getByLastName(String lastName);

    List<EmployeeDto> listAll();

    void deactivate(UUID employeeId);
}

