package com.example.demo.service;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 10:08
 */

import com.example.demo.dto.EmployeeDto;
import com.example.demo.entity.Employee;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDto create(EmployeeDto dto) {
        employeeRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
            throw new ApiException(ErrorCode.FIELD_INVALID, "employee.email", "Employee with given email already exists.");
        });

        Employee employee = EmployeeMapper.toEntity(dto);
        employee.setId(UUID.randomUUID());
        employee.setActive(dto.getActive() == null || dto.getActive());
        employee.setCreatedAt(Instant.now());
        employee.setUpdatedAt(Instant.now());

        Employee saved = employeeRepository.save(employee);
        return EmployeeMapper.toDto(saved);
    }

    @Override
    public EmployeeDto update(UUID employeeId, EmployeeDto dto) {
        Employee existing = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    throw new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "employee.id", "Employee not found or does not exist.");
                });

        // Business validation: email uniqueness (if changed)
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(existing.getEmail())) {
            employeeRepository.findByEmail(dto.getEmail()).ifPresent(other -> {
                throw new ApiException(ErrorCode.RESOURCE_CONFLICT, "employee.email", "Employee with given email already exists.");
            });
        }

        // Update mutable fields
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());
        existing.setHourlyRate(dto.getHourlyRate());

        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }

        existing.setUpdatedAt(Instant.now());

        Employee saved = employeeRepository.save(existing);
        return EmployeeMapper.toDto(saved);
    }

    @Override
    public EmployeeDto getById(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,"employeeId", "Employee not found."));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto getByFirstName(String firstName){
        Employee employee = employeeRepository.findByFirstName(firstName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,"firstName", "Employee not found."));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto getByLastName(String lastName){
        Employee employee = employeeRepository.findByLastName(lastName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,"lastName", "Employee not found."));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public List<EmployeeDto> listAll() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deactivate(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,"employeeId", "Employee not found."));

        if (employee.isActive()) {
            employee.setActive(false);
            employee.setUpdatedAt(Instant.now());
            employeeRepository.save(employee);
        }
    }

}

