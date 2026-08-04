package com.example.demo.entity;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:22
 */

import com.example.demo.enums.OfferStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offer {

    private UUID id;

    @NotBlank(message = "Offer number must not be blank")
    private String offerNumber;

    @NotNull(message = "Customer ID must not be null")
    private UUID customerId;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotBlank(message = "Currency must not be blank")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters (e.g., USD, EUR, CZK)")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a valid 3-letter ISO 4217 code")
    private String currency;

    @NotNull(message = "Price total must not be null")
    @Positive(message = "Price total must be positive")
    @DecimalMax(value = "999999999.99", message = "Price total must not exceed 999999999.99")
    private BigDecimal priceTotal;

    @Future(message = "Validity date must be in the future")
    private LocalDate validUntil;

    private OfferStatus status;

    private Instant createdAt;
    private Instant updatedAt;


}

