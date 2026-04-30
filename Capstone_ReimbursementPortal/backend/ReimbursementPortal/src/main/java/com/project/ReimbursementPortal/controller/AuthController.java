package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.dto.AuthRequestDto;
import com.project.ReimbursementPortal.dto.AuthResponseDto;
import com.project.ReimbursementPortal.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Authentication service for handling login logic. Injected via constructor.
     */
    private final AuthService authService;

    /**
     * Handles user login requests. Validates the request body and delegates authentication to the service layer.
     * @param request the login request containing email and password
     * @return a standard response containing authentication details if successful
     */
    @PostMapping("/login")
    public StandardResponseDto<AuthResponseDto> login(
            final @Valid @RequestBody AuthRequestDto request) {

        AuthResponseDto response = authService.login(request);

        return new StandardResponseDto<>(
                true,
                "Login successful",
                response
        );
    }
}
