package com.example.FreightGrid.web.exception;

import java.time.LocalDateTime;

/**
 * Structured error payload returned to the client whenever an exception is
 * handled by {@link GlobalExceptionHandler}.
 *
 * @param timestamp when the error occurred (server local time)
 * @param status    the HTTP status code of the response
 * @param error     the HTTP status reason phrase (e.g. "Forbidden")
 * @param message   human-readable detail about the failure
 */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
}
