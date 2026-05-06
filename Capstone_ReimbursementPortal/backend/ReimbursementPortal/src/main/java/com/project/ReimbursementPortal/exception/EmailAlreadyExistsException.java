package com.project.ReimbursementPortal.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Creates a new email already exists exception.
     * @param message error message
     */
    public EmailAlreadyExistsException(final String message) {
        super(message);
    }
}

