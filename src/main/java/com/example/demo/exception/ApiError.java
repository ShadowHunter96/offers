package com.example.demo.exception;

/**
 * API Error model representing a single error in a response.
 * Created by User: Vu
 * Date: 07.01.2026
 */
public class ApiError {

    private String code;
    private String scope;
    private String message;
    private ErrorCode errorCode;

    public ApiError(String code, String scope) {
        this.code = code;
        this.scope = scope;
    }

    public ApiError(String scope, String message, ErrorCode errorCode) {
        this.scope = scope;
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getCode() {
        return code;
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

