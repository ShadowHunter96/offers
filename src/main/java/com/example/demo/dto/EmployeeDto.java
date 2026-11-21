package com.example.demo.dto;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 9:52
 */

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDto {

    private UUID id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Size(max = 50, message = "Role must not exceed 50 characters")
    private String role;

    @Positive(message = "Hourly rate must be a positive number")
    @DecimalMax(value = "10000.00", message = "Hourly rate must not exceed 10000.00")
    private BigDecimal hourlyRate;

    private Boolean active;

}


