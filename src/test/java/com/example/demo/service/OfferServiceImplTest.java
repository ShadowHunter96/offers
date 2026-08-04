package com.example.demo.service;

import com.example.demo.dto.OfferDto;
import com.example.demo.entity.Offer;
import com.example.demo.enums.OfferStatus;
import com.example.demo.exception.BusinessRuleViolationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("OfferServiceImpl Tests")
class OfferServiceImplTest {

    private OfferServiceImpl offerService;

    @Mock
    private OfferRepository offerRepository;

    private Offer testOffer;
    private OfferDto testOfferDto;
    private UUID testOfferId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        offerService = new OfferServiceImpl(offerRepository);

        testOfferId = UUID.randomUUID();
        testOffer = new Offer();
        testOffer.setId(testOfferId);
        testOffer.setOfferNumber("OFF-2026-001");
        testOffer.setCustomerId(UUID.randomUUID());
        testOffer.setTitle("Test Offer");
        testOffer.setDescription("Test Description");
        testOffer.setCurrency("USD");
        testOffer.setPriceTotal(BigDecimal.valueOf(1000.00));
        testOffer.setValidUntil(LocalDate.now().plusDays(30));
        testOffer.setStatus(OfferStatus.DRAFT);
        testOffer.setCreatedAt(Instant.now());
        testOffer.setUpdatedAt(Instant.now());

        testOfferDto = new OfferDto();
        testOfferDto.setTitle("Test Offer");
        testOfferDto.setDescription("Test Description");
        testOfferDto.setCurrency("USD");
        testOfferDto.setPriceTotal(BigDecimal.valueOf(1000.00));
        testOfferDto.setValidUntil(LocalDate.now().plusDays(30));
        testOfferDto.setCustomerId(UUID.randomUUID());
    }

    @Test
    @DisplayName("create() should create new offer with DRAFT status")
    void testCreate() {
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto result = offerService.create(testOfferDto);

        assertNotNull(result);
        assertEquals("Test Offer", result.getTitle());
        assertEquals("USD", result.getCurrency());
        assertEquals(BigDecimal.valueOf(1000.00), result.getPriceTotal());
        assertEquals(OfferStatus.DRAFT, result.getStatus());
        assertNotNull(result.getId());

        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("update() should update offer when status is DRAFT")
    void testUpdateWhenDraft() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto updateDto = new OfferDto();
        updateDto.setTitle("Updated Title");
        updateDto.setCurrency("EUR");
        updateDto.setPriceTotal(BigDecimal.valueOf(2000.00));

        OfferDto result = offerService.update(testOfferId, updateDto);

        assertNotNull(result);
        verify(offerRepository, times(1)).findById(testOfferId);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("update() should update offer when status is SUBMITTED")
    void testUpdateWhenSubmitted() {
        testOffer.setStatus(OfferStatus.SUBMITTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto updateDto = new OfferDto();
        updateDto.setTitle("Updated Title");

        OfferDto result = offerService.update(testOfferId, updateDto);

        assertNotNull(result);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("update() should throw exception when offer status is ACCEPTED")
    void testUpdateWhenAccepted() {
        testOffer.setStatus(OfferStatus.ACCEPTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        OfferDto updateDto = new OfferDto();
        updateDto.setTitle("Updated Title");

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.update(testOfferId, updateDto)
        );

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("update() should throw exception when offer status is REJECTED")
    void testUpdateWhenRejected() {
        testOffer.setStatus(OfferStatus.REJECTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        OfferDto updateDto = new OfferDto();
        updateDto.setTitle("Updated Title");

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.update(testOfferId, updateDto)
        );
    }

    @Test
    @DisplayName("update() should throw exception when offer status is CANCELLED")
    void testUpdateWhenCancelled() {
        testOffer.setStatus(OfferStatus.CANCELLED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        OfferDto updateDto = new OfferDto();
        updateDto.setTitle("Updated Title");

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.update(testOfferId, updateDto)
        );
    }

    @Test
    @DisplayName("update() should throw exception when offer not found")
    void testUpdateNotFound() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.empty());

        OfferDto updateDto = new OfferDto();
        updateDto.setTitle("Updated Title");

        assertThrows(ResourceNotFoundException.class, () ->
            offerService.update(testOfferId, updateDto)
        );
    }

    @Test
    @DisplayName("getById() should return offer when found")
    void testGetById() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        OfferDto result = offerService.getById(testOfferId);

        assertNotNull(result);
        assertEquals(testOffer.getTitle(), result.getTitle());
        assertEquals(testOffer.getOfferNumber(), result.getOfferNumber());
        verify(offerRepository, times(1)).findById(testOfferId);
    }

    @Test
    @DisplayName("getById() should throw exception when offer not found")
    void testGetByIdNotFound() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            offerService.getById(testOfferId)
        );
    }

    @Test
    @DisplayName("listAll() should return all offers")
    void testListAll() {
        Offer offer2 = new Offer();
        offer2.setId(UUID.randomUUID());
        offer2.setOfferNumber("OFF-2026-002");
        offer2.setTitle("Offer 2");

        when(offerRepository.findAll()).thenReturn(Arrays.asList(testOffer, offer2));

        List<OfferDto> result = offerService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(offerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listAll() should return empty list when no offers")
    void testListAllEmpty() {
        when(offerRepository.findAll()).thenReturn(Arrays.asList());

        List<OfferDto> result = offerService.listAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("delete() should delete offer when status is DRAFT")
    void testDeleteWhenDraft() {
        testOffer.setStatus(OfferStatus.DRAFT);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        offerService.delete(testOfferId);

        verify(offerRepository, times(1)).deleteById(testOfferId);
    }

    @Test
    @DisplayName("delete() should throw exception when status is SUBMITTED")
    void testDeleteWhenSubmitted() {
        testOffer.setStatus(OfferStatus.SUBMITTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.delete(testOfferId)
        );

        verify(offerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete() should throw exception when status is ACCEPTED")
    void testDeleteWhenAccepted() {
        testOffer.setStatus(OfferStatus.ACCEPTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.delete(testOfferId)
        );
    }

    @Test
    @DisplayName("delete() should throw exception when offer not found")
    void testDeleteNotFound() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            offerService.delete(testOfferId)
        );
    }

    @Test
    @DisplayName("submit() should change status from DRAFT to SUBMITTED")
    void testSubmit() {
        testOffer.setStatus(OfferStatus.DRAFT);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto result = offerService.submit(testOfferId);

        assertNotNull(result);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("submit() should throw exception when status is not DRAFT")
    void testSubmitWhenNotDraft() {
        testOffer.setStatus(OfferStatus.SUBMITTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.submit(testOfferId)
        );

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("submit() should throw exception when offer not found")
    void testSubmitNotFound() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            offerService.submit(testOfferId)
        );
    }

    @Test
    @DisplayName("accept() should change status from SUBMITTED to ACCEPTED")
    void testAccept() {
        testOffer.setStatus(OfferStatus.SUBMITTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto result = offerService.accept(testOfferId);

        assertNotNull(result);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("accept() should throw exception when status is not SUBMITTED")
    void testAcceptWhenNotSubmitted() {
        testOffer.setStatus(OfferStatus.DRAFT);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.accept(testOfferId)
        );

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("accept() should throw exception when offer not found")
    void testAcceptNotFound() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            offerService.accept(testOfferId)
        );
    }

    @Test
    @DisplayName("reject() should change status from SUBMITTED to REJECTED")
    void testReject() {
        testOffer.setStatus(OfferStatus.SUBMITTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto result = offerService.reject(testOfferId);

        assertNotNull(result);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("reject() should throw exception when status is not SUBMITTED")
    void testRejectWhenNotSubmitted() {
        testOffer.setStatus(OfferStatus.DRAFT);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.reject(testOfferId)
        );

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("reject() should throw exception when offer not found")
    void testRejectNotFound() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            offerService.reject(testOfferId)
        );
    }

    @Test
    @DisplayName("cancel() should change status to CANCELLED when in DRAFT")
    void testCancelFromDraft() {
        testOffer.setStatus(OfferStatus.DRAFT);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto result = offerService.cancel(testOfferId);

        assertNotNull(result);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("cancel() should change status to CANCELLED when in SUBMITTED")
    void testCancelFromSubmitted() {
        testOffer.setStatus(OfferStatus.SUBMITTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto result = offerService.cancel(testOfferId);

        assertNotNull(result);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("cancel() should change status to CANCELLED when in ACCEPTED (non-terminal)")
    void testCancelWhenAccepted() {
        testOffer.setStatus(OfferStatus.ACCEPTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(any(Offer.class))).thenReturn(testOffer);

        OfferDto result = offerService.cancel(testOfferId);

        assertNotNull(result);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    @Test
    @DisplayName("cancel() should throw exception when status is REJECTED (terminal)")
    void testCancelWhenRejected() {
        testOffer.setStatus(OfferStatus.REJECTED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.cancel(testOfferId)
        );

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancel() should throw exception when status is CANCELLED (terminal)")
    void testCancelWhenAlreadyCancelled() {
        testOffer.setStatus(OfferStatus.CANCELLED);
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.of(testOffer));

        assertThrows(BusinessRuleViolationException.class, () ->
            offerService.cancel(testOfferId)
        );

        verify(offerRepository, never()).save(any());
    }

    @Test
    @DisplayName("cancel() should throw exception when offer not found")
    void testCancelNotFound() {
        when(offerRepository.findById(testOfferId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            offerService.cancel(testOfferId)
        );
    }
}
