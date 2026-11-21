package com.example.demo.exception;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 12:51
 */


public class BusinessRuleViolationException extends RuntimeException {

    private final String scope;

    public BusinessRuleViolationException(String scope, String message) {
        super(message);
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }
}

