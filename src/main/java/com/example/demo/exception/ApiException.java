package com.example.demo.exception;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 10:13
 */

public class ApiException extends RuntimeException {

    private final ErrorCode code;
    private final String scope;

    public ApiException(ErrorCode code, String scope, String message) {
        super(message);
        this.code = code;
        this.scope = scope;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getScope() {
        return scope;
    }
}

