package com.example.demo.controller;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 13:00
 */
import com.example.demo.dto.OfferDto;
import com.example.demo.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto createOffer(@Valid @RequestBody OfferDto dto) {
        return offerService.create(dto);
    }

    @PutMapping("/{offerId}")
    public OfferDto updateOffer(@PathVariable UUID offerId, @Valid @RequestBody OfferDto dto) {
        return offerService.update(offerId, dto);
    }

    @GetMapping("/{offerId}")
    public OfferDto getOffer(@PathVariable UUID offerId) {
        return offerService.getById(offerId);
    }

    @GetMapping
    public List<OfferDto> listOffers() {
        return offerService.listAll();
    }

    @DeleteMapping("/{offerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffer(@PathVariable UUID offerId) {
        offerService.delete(offerId);
    }

    @PostMapping("/{offerId}/submit")
    public OfferDto submitOffer(@PathVariable UUID offerId) {
        return offerService.submit(offerId);
    }

    @PostMapping("/{offerId}/accept")
    public OfferDto acceptOffer(@PathVariable UUID offerId) {
        return offerService.accept(offerId);
    }

    @PostMapping("/{offerId}/reject")
    public OfferDto rejectOffer(@PathVariable UUID offerId) {
        return offerService.reject(offerId);
    }

    @PostMapping("/{offerId}/cancel")
    public OfferDto cancelOffer(@PathVariable UUID offerId) {
        return offerService.cancel(offerId);
    }
}

