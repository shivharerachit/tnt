package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.AuthRequestDto;
import com.project.ReimbursementPortal.dto.AuthResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.exception.UnauthorizedException;
import com.project.ReimbursementPortal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service for user login.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * User repository for accessing user data from the database.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder for verifying user passwords. Injected via constructor.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user with email and password.
     *
     * @param request login request with email and password
     * @return authentication response with user details
     * @throws UnauthorizedException if credentials are invalid
     */
    public AuthResponseDto login(final AuthRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return new AuthResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
