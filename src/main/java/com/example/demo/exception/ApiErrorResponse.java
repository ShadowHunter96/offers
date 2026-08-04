package com.example.demo.exception;

import java.util.List;

/**
 * Created by User: Vu
 * Date: 07.01.2026
 * Time: 13:37
 */
public class ApiErrorResponse {

    private List<ApiError> errors;

    public ApiErrorResponse(List<ApiError> errors) {
        this.errors = errors;
    }

    public List<ApiError> getErrors() {
        return errors;
    }
}
