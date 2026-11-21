package com.example.demo.service;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:48
 */


import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

public class OfferNumberGenerator {

    private static final String PREFIX = "OFF";
    private final AtomicLong sequence = new AtomicLong(0);

    public String next() {
        long next = sequence.incrementAndGet();
        int year = Year.now().getValue();
        return PREFIX + "-" + year + "-" + String.format("%04d", next);
    }
}

