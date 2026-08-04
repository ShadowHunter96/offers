package com.example.demo.service;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:46
 */


import com.example.demo.dto.OfferDto;
import com.example.demo.entity.Offer;
import com.example.demo.enums.OfferStatus;
import com.example.demo.exception.BusinessRuleViolationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OfferRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferNumberGenerator offerNumberGenerator;

    public OfferServiceImpl(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
        this.offerNumberGenerator = new OfferNumberGenerator();
    }

    @Override
    public OfferDto create(OfferDto dto) {
        Offer offer = OfferMapper.toEntity(dto);

        offer.setId(UUID.randomUUID());
        offer.setOfferNumber(offerNumberGenerator.next());
        offer.setStatus(OfferStatus.DRAFT);

        Instant now = Instant.now();
        offer.setCreatedAt(now);
        offer.setUpdatedAt(now);

        Offer saved = offerRepository.save(offer);
        return OfferMapper.toDto(saved);
    }

    @Override
    public OfferDto update(UUID offerId, OfferDto dto) {
        Offer existing = getExisting(offerId);

        if (!isEditable(existing.getStatus())) {
            throw new BusinessRuleViolationException(
                    "offer.status",
                    "Offer cannot be updated in status: " + existing.getStatus()
            );
        }

        // Apply only updatable fields from DTO (status changes are done via dedicated endpoints)
        OfferMapper.applyUpdatableFields(existing, dto);
        existing.setUpdatedAt(Instant.now());

        Offer saved = offerRepository.save(existing);
        return OfferMapper.toDto(saved);
    }

    @Override
    public OfferDto getById(UUID offerId) {
        return OfferMapper.toDto(getExisting(offerId));
    }

    @Override
    public List<OfferDto> listAll() {
        return offerRepository.findAll()
                .stream()
                .map(OfferMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID offerId) {
        Offer existing = getExisting(offerId);

        if (existing.getStatus() != OfferStatus.DRAFT) {
            throw new BusinessRuleViolationException("offer.status", "Only DRAFT offers can be deleted.");
        }

        offerRepository.deleteById(offerId);
    }

    @Override
    public OfferDto submit(UUID offerId) {
        Offer existing = getExisting(offerId);

        if (existing.getStatus() != OfferStatus.DRAFT) {
            throw new BusinessRuleViolationException("offer.status", "Only DRAFT offers can be submitted.");
        }

        existing.setStatus(OfferStatus.SUBMITTED);
        existing.setUpdatedAt(Instant.now());

        return OfferMapper.toDto(offerRepository.save(existing));
    }

    @Override
    public OfferDto accept(UUID offerId) {
        Offer existing = getExisting(offerId);

        if (existing.getStatus() != OfferStatus.SUBMITTED) {
            throw new BusinessRuleViolationException("offer.status", "Only SUBMITTED offers can be accepted.");
        }

        existing.setStatus(OfferStatus.ACCEPTED);
        existing.setUpdatedAt(Instant.now());

        return OfferMapper.toDto(offerRepository.save(existing));
    }

    @Override
    public OfferDto reject(UUID offerId) {
        Offer existing = getExisting(offerId);

        if (existing.getStatus() != OfferStatus.SUBMITTED) {
            throw new BusinessRuleViolationException("offer.status", "Only SUBMITTED offers can be rejected.");
        }

        existing.setStatus(OfferStatus.REJECTED);
        existing.setUpdatedAt(Instant.now());

        return OfferMapper.toDto(offerRepository.save(existing));
    }

    @Override
    public OfferDto cancel(UUID offerId) {
        Offer existing = getExisting(offerId);

        if (isTerminal(existing.getStatus())) {
            throw new BusinessRuleViolationException(
                    "offer.status",
                    "Offer cannot be cancelled in terminal status: " + existing.getStatus()
            );
        }

        existing.setStatus(OfferStatus.CANCELLED);
        existing.setUpdatedAt(Instant.now());

        return OfferMapper.toDto(offerRepository.save(existing));
    }

    private Offer getExisting(UUID offerId) {
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("offerId", "Offer not found."));
    }

    private boolean isEditable(OfferStatus status) {
        return status == OfferStatus.DRAFT || status == OfferStatus.SUBMITTED;
    }

    private boolean isTerminal(OfferStatus status) {
        return status == OfferStatus.REJECTED || status == OfferStatus.CANCELLED;
    }
}

