package com.project.ReimbursementPortal.exception;

/**
 * Indicates that the current user is authenticated but not authorized for the action.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Creates a new forbidden exception.
     *
     * @param message error message
     */
    public ForbiddenException(final String message) {
        super(message);
    }
}

