package com.example.demo.exception;

/**
 * Created by User: Vu
 * Date: 06.01.2026
 * Time: 10:14
 */
public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String scope, String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, scope, message);
    }
}
