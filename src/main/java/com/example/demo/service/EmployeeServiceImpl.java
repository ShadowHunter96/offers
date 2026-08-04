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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDto create(EmployeeDto dto) {
        validateEmployeeDto(dto);
        
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
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        validateEmployeeDto(dto);
        
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
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,"employeeId", "Employee not found."));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto getByFirstName(String firstName){
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        
        Employee employee = employeeRepository.findByFirstName(firstName)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,"firstName", "Employee not found."));
        return EmployeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto getByLastName(String lastName){
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        
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
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND,"employeeId", "Employee not found."));

        if (employee.isActive()) {
            employee.setActive(false);
            employee.setUpdatedAt(Instant.now());
            employeeRepository.save(employee);
        }
    }

    /**
     * Validate employee DTO fields.
     */
    private void validateEmployeeDto(EmployeeDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Employee data cannot be null");
        }

        if (dto.getFirstName() == null || dto.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (dto.getFirstName().length() < 2 || dto.getFirstName().length() > 100) {
            throw new IllegalArgumentException("First name must be between 2 and 100 characters");
        }

        if (dto.getLastName() == null || dto.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (dto.getLastName().length() < 2 || dto.getLastName().length() > 100) {
            throw new IllegalArgumentException("Last name must be between 2 and 100 characters");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!isValidEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email must be a valid email address");
        }

        if (dto.getEmail().length() > 255) {
            throw new IllegalArgumentException("Email must not exceed 255 characters");
        }

        if (dto.getRole() != null && dto.getRole().length() > 50) {
            throw new IllegalArgumentException("Role must not exceed 50 characters");
        }

        if (dto.getHourlyRate() != null && dto.getHourlyRate().signum() <= 0) {
            throw new IllegalArgumentException("Hourly rate must be a positive number");
        }

        if (dto.getHourlyRate() != null && dto.getHourlyRate().compareTo(BigDecimal.valueOf(10000)) > 0) {
            throw new IllegalArgumentException("Hourly rate must not exceed 10000.00");
        }
    }

    /**
     * Validate email format.
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

}

