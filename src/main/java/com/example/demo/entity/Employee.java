package com.example.demo.entity;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 9:46
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private UUID id;

    private String firstName;
    private String lastName;
    private String email;

    private String role;
    private BigDecimal hourlyRate;

    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;





}

