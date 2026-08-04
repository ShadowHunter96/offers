package com.example.demo.dto;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 9:52
 */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    private String role;
    private BigDecimal hourlyRate;
    private Boolean active;

}

