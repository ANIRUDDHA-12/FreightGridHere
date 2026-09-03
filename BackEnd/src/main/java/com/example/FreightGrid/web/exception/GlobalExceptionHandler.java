package com.example.FreightGrid.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler that converts exceptions thrown by controllers
 * into structured {@link ErrorResponseDTO} responses.
 *
 * <p>Unauthorized access (both Spring Security {@link AccessDeniedException}
 * and the domain-specific {@link UnauthorizedStrategyException}) is mapped to
 * {@code 403 Forbidden}, while any other unexpected exception falls back to
 * {@code 500 Internal Server Error}.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Spring Security access-denied failures raised by method-level
     * security (e.g. {@code @PreAuthorize}).
     *
     * @param ex the access-denied exception
     * @return a {@code 403 Forbidden} response body
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Handles the domain-specific RBAC failure thrown when an Analyst attempts
     * to approve a financial strategy above their approval limit.
     *
     * @param ex the unauthorized-strategy exception
     * @return a {@code 403 Forbidden} response body
     */
    @ExceptionHandler(UnauthorizedStrategyException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedStrategy(UnauthorizedStrategyException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Fallback handler for any other exception not caught by a more specific
     * handler.
     *
     * @param ex the unexpected exception
     * @return a {@code 500 Internal Server Error} response body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String message) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage().contains("not found")) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    LocalDateTime.now(),
                    HttpStatus.NOT_FOUND.value(),
                    "NOT_FOUND",
                    ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        ErrorResponseDTO error = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

}
