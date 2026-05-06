package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.AuthRequestDto;
import com.project.ReimbursementPortal.dto.AuthResponseDto;
import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginShouldReturnSuccessResponse() {
        AuthRequestDto request = new AuthRequestDto("user@company.com", "password");
        AuthResponseDto authResponse = new AuthResponseDto(1L, "User", "user@company.com", "EMPLOYEE");
        when(authService.login(request)).thenReturn(authResponse);

        StandardResponseDto<AuthResponseDto> response = authController.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        assertEquals(1L, response.getData().getUserId());
        verify(authService).login(request);
    }
}
