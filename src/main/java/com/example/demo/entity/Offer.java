package com.example.demo.entity;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:22
 */

import com.example.demo.enums.OfferStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Offer number must not be blank")
    @Column(nullable = false, unique = true)
    private String offerNumber;

    @NotNull(message = "Customer ID must not be null")
    @Column(nullable = false)
    private UUID customerId;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(length = 2000)
    private String description;

    @NotBlank(message = "Currency must not be blank")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters (e.g., USD, EUR, CZK)")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a valid 3-letter ISO 4217 code")
    @Column(nullable = false, length = 3)
    private String currency;

    @NotNull(message = "Price total must not be null")
    @Positive(message = "Price total must be positive")
    @DecimalMax(value = "999999999.99", message = "Price total must not exceed 999999999.99")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal priceTotal;

    @FutureOrPresent(message = "Validity date must be today or in the future")
    @Column
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OfferStatus status;

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

