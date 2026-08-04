package com.example.demo.service;

import com.example.demo.dto.OfferDto;
import com.example.demo.entity.Offer;
import com.example.demo.enums.OfferStatus;

import java.time.Instant;
import java.util.UUID;

public final class OfferMapper {

    private OfferMapper() {
    }

    /**
     * Maps Offer entity to OfferDto.
     */
    public static OfferDto toDto(Offer offer) {
        OfferDto dto = new OfferDto();
        dto.setId(offer.getId());
        dto.setOfferNumber(offer.getOfferNumber());
        dto.setCustomerId(offer.getCustomerId());
        dto.setTitle(offer.getTitle());
        dto.setDescription(offer.getDescription());
        dto.setCurrency(offer.getCurrency());
        dto.setPriceTotal(offer.getPriceTotal());
        dto.setValidUntil(offer.getValidUntil());
        dto.setStatus(offer.getStatus() != null ? offer.getStatus() : null);
        return dto;
    }

    /**
     * Creates new Offer entity from OfferDto.
     * Does NOT handle status transitions or timestamps.
     */
    public static Offer toEntity(OfferDto dto) {
        Offer offer = new Offer();

        // ID is generated in service layer
        if (dto.getId() != null) {
            offer.setId(dto.getId());
        }

        offer.setCustomerId(dto.getCustomerId());
        offer.setTitle(dto.getTitle());
        offer.setDescription(dto.getDescription());
        offer.setCurrency(dto.getCurrency());
        offer.setPriceTotal(dto.getPriceTotal());
        offer.setValidUntil(dto.getValidUntil());
        offer.setStatus(dto.getStatus());

        return offer;
    }

    /**
     * Applies updatable fields from DTO to existing entity.
     * Used for update operations.
     */
    public static void applyUpdatableFields(Offer offer, OfferDto dto) {
        offer.setCustomerId(dto.getCustomerId());
        offer.setTitle(dto.getTitle());
        offer.setDescription(dto.getDescription());
        offer.setCurrency(dto.getCurrency());
        offer.setPriceTotal(dto.getPriceTotal());
        offer.setValidUntil(dto.getValidUntil());
    }
}
