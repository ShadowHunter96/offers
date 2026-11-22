package com.example.demo.controller;

import com.example.demo.dto.OfferDto;
import com.example.demo.enums.OfferStatus;
import com.example.demo.exception.BusinessRuleViolationException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.ApiSecurityErrorHandler;
import com.example.demo.security.SecurityConfig;
import com.example.demo.service.OfferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer slice tests: URL mapping, JSON (de)serialisation, bean validation,
 * exception-to-status mapping and security rules, with the service layer mocked.
 */
@WebMvcTest(OfferController.class)
@Import({SecurityConfig.class, ApiSecurityErrorHandler.class})
@DisplayName("OfferController (MockMvc) Tests")
class OfferControllerWebMvcTest {

    private static final RequestPostProcessor MANAGER = httpBasic("manager", "manager123");
    private static final RequestPostProcessor VIEWER = httpBasic("viewer", "viewer123");
    private static final String VALID_BODY = """
            {"customerId":"00000000-0000-0000-0000-000000000001","title":"API implementation",
             "currency":"EUR","priceTotal":123.45}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private OfferService offerService;

    private OfferDto offer(OfferStatus status) {
        OfferDto dto = new OfferDto();
        dto.setId(UUID.randomUUID());
        dto.setOfferNumber("OFF-2026-0001");
        dto.setTitle("API implementation");
        dto.setCurrency("EUR");
        dto.setPriceTotal(new BigDecimal("123.45"));
        dto.setStatus(status);
        return dto;
    }

    @Test
    @DisplayName("POST /api/offers returns 201 and the created offer")
    void createReturns201() throws Exception {
        when(offerService.create(any())).thenReturn(offer(OfferStatus.DRAFT));

        mvc.perform(post("/api/offers").with(MANAGER).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.offerNumber").value("OFF-2026-0001"));
    }

    @Test
    @DisplayName("invalid body returns 400 with all invalid fields and the service is not called")
    void invalidBodyReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/offers").with(MANAGER).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"ab\",\"currency\":\"czk\",\"priceTotal\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(4))
                .andExpect(jsonPath("$.errors[*].errorCode").value(org.hamcrest.Matchers.everyItem(
                        org.hamcrest.Matchers.equalTo("FIELD_INVALID"))));
        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("ResourceNotFoundException is mapped to 404 RESOURCE_NOT_FOUND")
    void notFoundIsMappedTo404() throws Exception {
        UUID id = UUID.randomUUID();
        when(offerService.getById(id)).thenThrow(new ResourceNotFoundException("offerId", "Offer not found."));

        mvc.perform(get("/api/offers/{id}", id).with(VIEWER))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/offers/" + id));
    }

    @Test
    @DisplayName("BusinessRuleViolationException is mapped to 409 BUSINESS_RULE_VIOLATION")
    void businessRuleIsMappedTo409() throws Exception {
        UUID id = UUID.randomUUID();
        when(offerService.accept(id)).thenThrow(
                new BusinessRuleViolationException("offer.status", "Only SUBMITTED offers can be accepted."));

        mvc.perform(post("/api/offers/{id}/accept", id).with(MANAGER))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0].scope").value("offer.status"))
                .andExpect(jsonPath("$.errors[0].errorCode").value("BUSINESS_RULE_VIOLATION"));
    }

    @Test
    @DisplayName("unsupported HTTP method returns 405")
    void unsupportedMethodReturns405() throws Exception {
        mvc.perform(patch("/api/offers/{id}", UUID.randomUUID()).with(MANAGER)
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("unsupported content type returns 415")
    void unsupportedMediaTypeReturns415() throws Exception {
        mvc.perform(post("/api/offers").with(MANAGER).contentType(MediaType.TEXT_PLAIN).content("offer"))
                .andExpect(status().isUnsupportedMediaType());
        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("request without credentials returns 401 AUTHENTICATION_REQUIRED")
    void missingCredentialsReturns401() throws Exception {
        mvc.perform(get("/api/offers"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", org.hamcrest.Matchers.containsString("Basic")))
                .andExpect(jsonPath("$.errors[0].errorCode").value("AUTHENTICATION_REQUIRED"));
        verifyNoInteractions(offerService);
    }

    @Test
    @DisplayName("viewer may list offers")
    void viewerCanList() throws Exception {
        when(offerService.listAll()).thenReturn(List.of(offer(OfferStatus.DRAFT)));

        mvc.perform(get("/api/offers").with(VIEWER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("viewer may not change state of an offer (403 ACCESS_DENIED)")
    void viewerCannotSubmit() throws Exception {
        mvc.perform(post("/api/offers/{id}/submit", UUID.randomUUID()).with(VIEWER))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errors[0].errorCode").value("ACCESS_DENIED"));
        verify(offerService, never()).submit(any());
    }
}
