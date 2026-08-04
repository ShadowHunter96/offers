package com.example.demo.entity;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:22
 */

import com.example.demo.enums.OfferStatus;
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

    private String offerNumber;

    private UUID customerId;

    private String title;
    private String description;

    private String currency;
    private BigDecimal priceTotal;

    private LocalDate validUntil;

    private OfferStatus status;

    private Instant createdAt;
    private Instant updatedAt;


}

