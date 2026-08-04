package com.example.demo.repository.inmemory;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:27
 */

import com.example.demo.entity.Offer;
import com.example.demo.repository.OfferRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOfferRepository implements OfferRepository {

    private final Map<UUID, Offer> storage = new ConcurrentHashMap<>();

    @Override
    public Offer save(Offer offer) {
        storage.put(offer.getId(), offer);
        return offer;
    }

    @Override
    public Optional<Offer> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Offer> findByOfferNumber(String offerNumber) {
        if (offerNumber == null) {
            return Optional.empty();
        }

        return storage.values()
                .stream()
                .filter(o -> offerNumber.equalsIgnoreCase(o.getOfferNumber()))
                .findFirst();
    }

    @Override
    public List<Offer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}

