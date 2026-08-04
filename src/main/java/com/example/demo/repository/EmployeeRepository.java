package com.example.demo.repository;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 10:01
 */

import com.example.demo.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(UUID id);
    Optional<Employee> findByFirstName(String firstName);
    Optional<Employee> findByLastName(String lastName);

    Optional<Employee> findByEmail(String email);

    List<Employee> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}

