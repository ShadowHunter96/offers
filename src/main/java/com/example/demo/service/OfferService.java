package com.example.demo.service;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:45
 */
import com.example.demo.dto.OfferDto;

import java.util.List;
import java.util.UUID;

public interface OfferService {

    OfferDto create(OfferDto dto);

    OfferDto update(UUID offerId, OfferDto dto);

    OfferDto getById(UUID offerId);

    List<OfferDto> listAll();

    void delete(UUID offerId);

    OfferDto submit(UUID offerId);

    OfferDto accept(UUID offerId);

    OfferDto reject(UUID offerId);

    OfferDto cancel(UUID offerId);
}

