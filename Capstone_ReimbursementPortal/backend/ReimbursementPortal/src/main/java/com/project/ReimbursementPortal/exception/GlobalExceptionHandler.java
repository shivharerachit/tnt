package com.project.ReimbursementPortal.exception;

import com.project.ReimbursementPortal.dto.StandardResponseDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param ex not found
     * @return 404 + message
     */
    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<StandardResponseDto<?>> handleClaimNotFound(final ClaimNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * @param ex not found
     * @return 404 + message
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StandardResponseDto<?>> handleUserNotFound(final UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * @param ex wrong role / rule
     * @return 403
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardResponseDto<?>> handleForbidden(final ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * @param ex bad login etc.
     * @return 401
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardResponseDto<?>> handleUnauthorized(final UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * @param ex bad input / business rule from our code
     * @return 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StandardResponseDto<?>> handleBadRequest(final BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * @param ex duplicate email signup
     * @return 409
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<StandardResponseDto<?>> handleEmailAlreadyExists(final EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new StandardResponseDto<>(false, ex.getMessage(), null));
    }

    /**
     * Postgres unique constraint etc. bubbling up through Spring.
     *
     * @param ex constraint failure
     * @return 409 + short message prefix
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
     * Bean validation on DTOs (@NotNull etc.).
     *
     * @param ex field errors
     * @return one 400 string joining all fields
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
     * Last resort — anything else.
     *
     * @param ex whatever bubbled up
     * @return 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponseDto<?>> handleException(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StandardResponseDto<>(false, ex.getMessage() != null ? ex.getMessage() : "Something went wrong", null));
    }
}
