package com.example.demo.exception;

/**
 * API Error model representing a single error in a response.
 * Created by User: Vu
 * Date: 07.01.2026
 */
public class ApiError {

    private String scope;
    private String message;
    private ErrorCode errorCode;

    public ApiError(String scope, String message, ErrorCode errorCode) {
        this.scope = scope;
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getScope() {
        return scope;
    }

    public String getMessage() {
        return message;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

