package com.example.demo.repository.inmemory;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 10:02
 */
import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<UUID, Employee> storage = new ConcurrentHashMap<>();

    @Override
    public Employee save(Employee employee) {
        storage.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return storage.values()
                .stream()
                .filter(e -> email.equalsIgnoreCase(e.getEmail()))
                .findFirst();
    }

    @Override
    public Optional<Employee> findByFirstName(String firstName) {
        if (firstName == null) {
            return Optional.empty();
        }
        return storage.values()
                .stream()
                .filter(e -> firstName.equalsIgnoreCase(e.getFirstName()))
                .findFirst();
    }

    @Override
    public Optional<Employee> findByLastName(String lastName) {
        if (lastName == null) {
            return Optional.empty();
        }
        return storage.values()
                .stream()
                .filter(e -> lastName.equalsIgnoreCase(e.getLastName()))
                .findFirst();
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}

