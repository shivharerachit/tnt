package com.project.ReimbursementPortal.exception;

/**
 * Indicates that the client sent an invalid request.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates a new bad request exception.
     *
     * @param message error message
     */
    public BadRequestException(final String message) {
        super(message);
    }
}

