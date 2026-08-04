package com.example.demo.repository;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:26
 */
import com.example.demo.entity.Offer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferRepository {

    Offer save(Offer offer);

    Optional<Offer> findById(UUID id);

    Optional<Offer> findByOfferNumber(String offerNumber);

    List<Offer> findAll();

    void deleteById(UUID id);
}

