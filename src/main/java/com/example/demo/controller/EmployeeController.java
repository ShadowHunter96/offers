package com.example.demo.controller;

import com.example.demo.dto.EmployeeDto;
import com.example.demo.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing employees.
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Creates a new employee.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto createEmployee(@Valid @RequestBody EmployeeDto dto) {
        return employeeService.create(dto);
    }

    /**
     * Updates an existing employee.
     */
    @PutMapping("/{employeeId}")
    public EmployeeDto updateEmployee(
            @PathVariable UUID employeeId,
            @Valid @RequestBody EmployeeDto dto
    ) {
        return employeeService.update(employeeId, dto);
    }

    /**
     * Returns employee detail by ID.
     */
    @GetMapping("/id/{employeeId}")
    public EmployeeDto getEmployeeById(@PathVariable UUID employeeId) {
        return employeeService.getById(employeeId);
    }

    /**
     * Returns employee detail by firstName.
     */
    @GetMapping("/firstName/{firstName}")
    public EmployeeDto getEmployeeByFirstName(@PathVariable String firstName) {
        return employeeService.getByFirstName(firstName);
    }

    /**
     * Returns employee detail by firstName.
     */
    @GetMapping("/lastName/{lastName}")
    public EmployeeDto getEmployeeByLastName(@PathVariable String lastName) {
        return employeeService.getByLastName(lastName);
    }
    /**
     * Returns list of all employees.
     */
    @GetMapping
    public List<EmployeeDto> listEmployees() {
        return employeeService.listAll();
    }

    /**
     * Deactivates an employee (soft delete).
     */
    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateEmployee(@PathVariable UUID employeeId) {
        employeeService.deactivate(employeeId);
    }
}
