package com.example.demo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiExceptionHandler Tests")
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();
    private final ServletWebRequest request =
            new ServletWebRequest(new MockHttpServletRequest("POST", "/api/offers"));

    @Test
    @DisplayName("unexpected exception is mapped to 500 with unified body and no internal details")
    void mapsUnexpectedExceptionToUnifiedInternalError() {
        ResponseEntity<Object> response =
                handler.handleUnexpected(new IllegalStateException("db password=secret"), request);

        assertEquals(500, response.getStatusCode().value());
        ApiErrorResponse body = assertInstanceOf(ApiErrorResponse.class, response.getBody());
        assertEquals("/api/offers", body.getPath());
        assertEquals(ErrorCode.INTERNAL_ERROR, body.getErrors().get(0).getErrorCode());
        assertFalse(body.getErrors().get(0).getMessage().contains("secret"));
    }

    @Test
    @DisplayName("database constraint violation is mapped to 409 RESOURCE_CONFLICT")
    void mapsDataIntegrityViolationToConflict() {
        ResponseEntity<Object> response =
                handler.handleDataIntegrity(new DataIntegrityViolationException("unique"), request);

        assertEquals(409, response.getStatusCode().value());
        ApiErrorResponse body = assertInstanceOf(ApiErrorResponse.class, response.getBody());
        assertEquals(ErrorCode.RESOURCE_CONFLICT, body.getErrors().get(0).getErrorCode());
    }
}
