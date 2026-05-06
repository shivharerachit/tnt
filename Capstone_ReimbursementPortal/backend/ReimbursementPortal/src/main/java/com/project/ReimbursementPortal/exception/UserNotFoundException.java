package com.project.ReimbursementPortal.exception;

public class UserNotFoundException extends RuntimeException {

    /**
     * Creates a new user not found exception.
     * @param message error message
     */
    public UserNotFoundException(final String message) {
        super(message);
    }
}

