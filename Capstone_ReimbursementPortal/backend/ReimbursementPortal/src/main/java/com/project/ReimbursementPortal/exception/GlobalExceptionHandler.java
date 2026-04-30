package com.project.ReimbursementPortal.exception;

import com.project.ReimbursementPortal.dto.StandardResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for centralized error handling and consistent HTTP status codes.
 * Maps domain-specific exceptions to appropriate HTTP status codes:
 * - 400 Bad Request: Invalid client input or request format
 * - 401 Unauthorized: Missing or invalid authentication
 * - 403 Forbidden: Authenticated but lacks authorization
 * - 404 Not Found: Resource doesn't exist
 * - 409 Conflict: Duplicate resource or constraint violation
 * - 500 Internal Server Error: Unexpected server errors
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles claim not found (404 Not Found).
     * @param ex the exception thrown when a claim is not found
     * @return a response entity with a standard response body and 404 status
     */
    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<StandardResponseDto<?>> handleClaimNotFound(final ClaimNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * Handles user not found (404 Not Found).
     * @param ex the exception thrown when a user is not found
     * @return a response entity with a standard response body and 404 status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardResponseDto<?>> handleUserNotFound(final UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * Handles forbidden access (403 Forbidden) - user is authenticated but not authorized.
     * @param ex the exception thrown when access is forbidden
     * @return a response entity with a standard response body and 403 status
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardResponseDto<?>> handleForbidden(final ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * Handles unauthorized access (401 Unauthorized) - missing or invalid authentication.
     * @param ex the exception thrown when authentication fails
     * @return a response entity with a standard response body and 401 status
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardResponseDto<?>> handleUnauthorized(final UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * Handles bad request (400 Bad Request) - invalid client input.
     * @param ex the exception thrown when a bad request is made
     * @return a response entity with a standard response body and 400 status
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StandardResponseDto<?>> handleBadRequest(final BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * Handles email already exists (409 Conflict).
     * @param ex the exception thrown when trying to create a user with an email that already exists
     * @return a response entity with a standard response body and 409 status
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<StandardResponseDto<?>> handleEmailAlreadyExists(final EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * Handles data integrity violations (409 Conflict).
     * @param ex the exception thrown when a database constraint is violated (e.g., unique constraint)
     * @return a response entity with a standard response body and 409 status, including the
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardResponseDto<?>> handleDataIntegrityViolation(final DataIntegrityViolationException ex) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String message = mostSpecificCause.getMessage() != null
                ? mostSpecificCause.getMessage()
                : ex.getMessage() != null ? ex.getMessage() : "Data integrity violation";

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new StandardResponseDto<>(false, "Database constraint violation: " + message, null));
    }

    /**
     * Handles validation errors (400 Bad Request).
     * @param ex the exception thrown when method arguments fail validation
     * @return a response entity with a standard response body and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponseDto<?>> handleValidationExceptions(final MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponseDto<>(false, "Validation Error: " + message, null));
    }

    /**
     * Fallback handler for unexpected exceptions (500 Internal Server Error).
     * @param ex the exception thrown when an unexpected error occurs
     * @return a response entity with a standard response body and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponseDto<?>> handleException(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StandardResponseDto<>(false, ex.getMessage() != null ? ex.getMessage() : "Something went wrong", null));
    }
}
