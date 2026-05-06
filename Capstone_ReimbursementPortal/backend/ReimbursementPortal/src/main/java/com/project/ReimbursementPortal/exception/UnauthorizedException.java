package com.project.ReimbursementPortal.exception;

/**
 * Indicates that the request is not authenticated (or credentials are invalid).
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Creates a new unauthorized exception.
     *
     * @param message error message
     */
    public UnauthorizedException(final String message) {
        super(message);
    }
}

