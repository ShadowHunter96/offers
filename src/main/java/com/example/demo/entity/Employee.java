package com.example.demo.entity;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 9:46
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Size(max = 50, message = "Role must not exceed 50 characters")
    @Column(length = 50)
    private String role;

    @Positive(message = "Hourly rate must be positive")
    @DecimalMax(value = "10000.00", message = "Hourly rate must not exceed 10000.00")
    @Column(precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}


