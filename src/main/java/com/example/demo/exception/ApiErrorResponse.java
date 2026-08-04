package com.example.demo.exception;

import java.time.Instant;
import java.util.List;

/**
 * API Error Response model for REST endpoints.
 * Created by User: Vu
 * Date: 07.01.2026
 */
public class ApiErrorResponse {

    private int status;
    private String message;
    private Instant timestamp;
    private String path;
    private List<ApiError> errors;

    public ApiErrorResponse(List<ApiError> errors) {
        this.errors = errors;
    }

    public ApiErrorResponse(int status, String message, Instant timestamp, String path, List<ApiError> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public List<ApiError> getErrors() {
        return errors;
    }
}

