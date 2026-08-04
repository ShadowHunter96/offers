package com.example.demo.dto;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:23
 */
import com.example.demo.enums.OfferStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferDto {

    private UUID id;

    private String offerNumber;

    private UUID customerId;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String currency;

    @NotNull
    private BigDecimal priceTotal;

    private LocalDate validUntil;

    private OfferStatus status;


}

