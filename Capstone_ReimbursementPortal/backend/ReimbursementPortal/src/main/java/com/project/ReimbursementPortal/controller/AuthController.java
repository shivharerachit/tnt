package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.dto.AuthRequestDto;
import com.project.ReimbursementPortal.dto.AuthResponseDto;
import com.project.ReimbursementPortal.service.AuthService;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    /**
     * Service for handling authentication logic, such as verifying user credentials and generating response data.
     */
    private final AuthService authService;

    /**
     * @param authService service
     */
    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    /**
     * @param request email and password JSON
     * @return profile fields your frontend sticks in session
     */
    @PostMapping("/login")
    public StandardResponseDto<AuthResponseDto> login(
            final @Valid @RequestBody AuthRequestDto request) {

        log.info("POST /auth/login email={}", request.getEmail());

        AuthResponseDto response = authService.login(request);

        log.info("POST /auth/login success email={}", request.getEmail());

        return new StandardResponseDto<>(
                true,
                "Login successful",
                response
        );
    }
}
