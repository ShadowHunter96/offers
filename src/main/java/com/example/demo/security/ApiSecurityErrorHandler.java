package com.example.demo.security;

import com.example.demo.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Writes 401/403 responses from the security filter chain in the same structure as
 * {@link com.example.demo.exception.ApiErrorResponse}. The filter chain runs before Spring MVC,
 * so {@code @RestControllerAdvice} cannot handle these errors.
 */
@Component
public class ApiSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"offers\"");
        write(response, request, HttpStatus.UNAUTHORIZED, "Authentication is required.",
                ErrorCode.AUTHENTICATION_REQUIRED);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        write(response, request, HttpStatus.FORBIDDEN, "Insufficient permissions for this operation.",
                ErrorCode.ACCESS_DENIED);
    }

    private void write(HttpServletResponse response, HttpServletRequest request, HttpStatus status,
                       String message, ErrorCode code) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // messages are constants; the only client-supplied value (request path) is escaped
        String body = """
                {"status":%d,"message":"%s","timestamp":"%s","path":"%s","errors":[{"scope":"request","message":"%s","errorCode":"%s"}]}"""
                .formatted(status.value(), status.getReasonPhrase(), Instant.now(),
                        escape(request.getRequestURI()), message, code.name());
        response.getWriter().write(body);
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
