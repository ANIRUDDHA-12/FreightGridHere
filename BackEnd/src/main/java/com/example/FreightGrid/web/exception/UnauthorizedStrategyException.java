package com.example.FreightGrid.web.exception;

/**
 * Thrown when an Analyst attempts to approve a financial strategy that
 * exceeds their RBAC approval limit.
 *
 * <p>The exception is handled globally by {@link GlobalExceptionHandler}
 * and surfaced to the client as an HTTP {@code 403 Forbidden} response.</p>
 */
public class UnauthorizedStrategyException extends RuntimeException {

    /**
     * Creates an {@code UnauthorizedStrategyException} with the given message.
     *
     * @param message detail about why the strategy approval was rejected
     */
    public UnauthorizedStrategyException(String message) {
        super(message);
    }
}
