package com.project.ReimbursementPortal.exception;

/**
 * Indicates that a claim could not be found.
 */
public class ClaimNotFoundException extends RuntimeException {

    /**
     * Creates a new claim not found exception.
     *
     * @param message error message
     */
    public ClaimNotFoundException(final String message) {
        super(message);
    }
}

