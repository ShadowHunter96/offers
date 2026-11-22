package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception, WebRequest request) {
        HttpStatus status = switch (exception.getCode()) {
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case RESOURCE_CONFLICT, BUSINESS_RULE_VIOLATION -> HttpStatus.CONFLICT;
            case FIELD_INVALID -> HttpStatus.BAD_REQUEST;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case AUTHENTICATION_REQUIRED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
        };
        return response(status, new HttpHeaders(), request,
                List.of(new ApiError(exception.getScope(), exception.getMessage(), exception.getCode())));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<Object> handleBusinessRule(BusinessRuleViolationException exception,
                                                       WebRequest request) {
        return response(HttpStatus.CONFLICT, new HttpHeaders(), request,
                List.of(new ApiError(exception.getScope(), exception.getMessage(),
                        ErrorCode.BUSINESS_RULE_VIOLATION)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleInvalidArgument(IllegalArgumentException exception,
                                                         WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, new HttpHeaders(), request,
                List.of(new ApiError("request", exception.getMessage(), ErrorCode.FIELD_INVALID)));
    }

    /**
     * Database constraint violated despite service-level checks (e.g. two concurrent
     * requests creating the same e-mail) - reported as a conflict, not as a server error.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException exception,
                                                      WebRequest request) {
        return response(HttpStatus.CONFLICT, new HttpHeaders(), request,
                List.of(new ApiError("request", "Request conflicts with existing data.",
                        ErrorCode.RESOURCE_CONFLICT)));
    }

    /**
     * Fallback for unexpected errors: keeps the unified ApiErrorResponse structure
     * and does not expose internal details (stack trace, exception message) to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception exception, WebRequest request) {
        log.error("Unexpected error while processing {}", request.getDescription(false), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, new HttpHeaders(), request,
                List.of(new ApiError("request", "Unexpected server error.", ErrorCode.INTERNAL_ERROR)));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                   HttpHeaders headers,
                                                                   HttpStatusCode status,
                                                                   WebRequest request) {
        List<ApiError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError(error.getField(), error.getDefaultMessage(), ErrorCode.FIELD_INVALID))
                .toList();
        return response(status, headers, request, errors);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
                                                               HttpHeaders headers, HttpStatusCode status,
                                                               WebRequest request) {
        HttpStatus knownStatus = HttpStatus.resolve(status.value());
        String message = knownStatus == null ? "Request failed" : knownStatus.getReasonPhrase();
        ErrorCode code = status.value() == 404 ? ErrorCode.RESOURCE_NOT_FOUND : ErrorCode.FIELD_INVALID;
        return response(status, headers, request, List.of(new ApiError("request", message, code)));
    }

    private ResponseEntity<Object> response(HttpStatusCode status, HttpHeaders headers,
                                           WebRequest request, List<ApiError> errors) {
        HttpStatus knownStatus = HttpStatus.resolve(status.value());
        String message = knownStatus == null ? "Request failed" : knownStatus.getReasonPhrase();
        String path = request.getDescription(false).replaceFirst("^uri=", "");
        return new ResponseEntity<>(new ApiErrorResponse(status.value(), message, Instant.now(), path, errors),
                headers, status);
    }
}