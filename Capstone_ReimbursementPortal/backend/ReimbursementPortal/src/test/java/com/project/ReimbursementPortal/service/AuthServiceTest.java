package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.AuthRequestDto;
import com.project.ReimbursementPortal.dto.AuthResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.UnauthorizedException;
import com.project.ReimbursementPortal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginShouldReturnResponseWhenCredentialsAreValid() {
        AuthRequestDto request = new AuthRequestDto("test@company.com", "plain-password");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@company.com");
        user.setPassword("encoded-password");
        user.setRole(UserRole.EMPLOYEE);

        when(userRepository.findByEmail("test@company.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain-password", "encoded-password")).thenReturn(true);

        AuthResponseDto response = authService.login(request);

        assertEquals(1L, response.getUserId());
        assertEquals("Test User", response.getName());
        assertEquals("test@company.com", response.getEmail());
        assertEquals("EMPLOYEE", response.getRole());
        verify(userRepository).findByEmail("test@company.com");
        verify(passwordEncoder).matches("plain-password", "encoded-password");
    }

    @Test
    void loginShouldThrowWhenEmailDoesNotExist() {
        AuthRequestDto request = new AuthRequestDto("missing@company.com", "plain-password");
        when(userRepository.findByEmail("missing@company.com")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail("missing@company.com");
    }

    @Test
    void loginShouldThrowWhenPasswordIsIncorrect() {
        AuthRequestDto request = new AuthRequestDto("test@company.com", "wrong-password");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@company.com");
        user.setPassword("encoded-password");
        user.setRole(UserRole.EMPLOYEE);

        when(userRepository.findByEmail("test@company.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));

        assertEquals("Invalid email or password", exception.getMessage());
        verify(passwordEncoder).matches("wrong-password", "encoded-password");
    }
}