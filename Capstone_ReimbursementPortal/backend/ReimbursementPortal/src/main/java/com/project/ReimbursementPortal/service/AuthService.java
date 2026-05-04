package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.AuthRequestDto;
import com.project.ReimbursementPortal.dto.AuthResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.exception.UnauthorizedException;
import com.project.ReimbursementPortal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Repository for accessing user data, used to find users by email during login.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder for verifying plaintext passwords against stored hashed passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * @param request email + plaintext password from request body
     * @return identity fields only (still need X-USER-ID on later calls — no JWT in this project)
     * @throws UnauthorizedException wrong credentials
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
