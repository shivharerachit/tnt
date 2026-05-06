package com.project.ReimbursementPortal.exception;

import com.project.ReimbursementPortal.dto.StandardResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUserNotFoundShouldReturnNotFound() {
        ResponseEntity<StandardResponseDto<?>> response =
                handler.handleUserNotFound(new UserNotFoundException("User not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleBadRequestShouldReturnBadRequest() {
        ResponseEntity<StandardResponseDto<?>> response =
                handler.handleBadRequest(new BadRequestException("Invalid request"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid request", response.getBody().getMessage());
    }

    @Test
    void handleDataIntegrityViolationShouldReturnConflictWithPrefix() {
        RuntimeException rootCause = new RuntimeException("duplicate key value violates unique constraint");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint", rootCause);

        ResponseEntity<StandardResponseDto<?>> response = handler.handleDataIntegrityViolation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().startsWith("Database constraint violation: "));
    }

    @Test
    void handleExceptionShouldReturnInternalServerError() {
        ResponseEntity<StandardResponseDto<?>> response = handler.handleException(new RuntimeException("Boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Boom", response.getBody().getMessage());
    }
}
