package com.example.demo.repository;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:26
 */
import com.example.demo.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OfferRepository extends JpaRepository<Offer, UUID> {

    Optional<Offer> findByOfferNumber(String offerNumber);
}

