package com.spring.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;

    public ErrorResponse(int status, String error) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
    }
}
