package com.example.demo.exception;

/**
 * Created by User: Vu
 * Date: 07.01.2026
 * Time: 13:37
 */
public class ApiError {

    private String code;
    private String scope;

    public ApiError(String code, String scope) {
        this.code = code;
        this.scope = scope;
    }

    public String getCode() {
        return code;
    }

    public String getScope() {
        return scope;
    }
}
