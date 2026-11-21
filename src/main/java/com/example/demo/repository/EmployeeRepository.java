package com.example.demo.repository;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 10:01
 */

import com.example.demo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByFirstName(String firstName);
    Optional<Employee> findByLastName(String lastName);

    Optional<Employee> findByEmail(String email);
}

