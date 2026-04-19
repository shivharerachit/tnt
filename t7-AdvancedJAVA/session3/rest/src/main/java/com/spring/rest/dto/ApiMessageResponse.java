package com.spring.rest.dto;

import lombok.Getter;

@Getter
public class ApiMessageResponse {
    private final String message;

    public ApiMessageResponse(String message) {
        this.message = message;
    }

}
