package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.ChangePasswordRequestDto;
import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.repository.UserRepository;
import com.project.ReimbursementPortal.service.UserService;
import com.project.ReimbursementPortal.validator.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailValidator emailValidator;

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, passwordEncoder, emailValidator);
        userController = new UserController(userService);
    }

    @Test
    void createUserShouldReturnSuccessResponse() {
        UserRequestDto req = new UserRequestDto();
        req.setName("New User");
        req.setEmail("new.user@company.com");
        req.setPassword("password");
        req.setRole("EMPLOYEE");

        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findByEmail("new.user@company.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        User saved = new User();
        saved.setId(10L);
        saved.setName("New User");
        saved.setEmail("new.user@company.com");
        saved.setRole(UserRole.EMPLOYEE);
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);

        StandardResponseDto<UserResponseDto> response = userController.createUser(req, "1");

        assertTrue(response.isSuccess());
        assertEquals("User created successfully", response.getMessage());
        assertEquals(10L, response.getData().getId());
    }

    @Test
    void getUsersByRoleShouldReturnFilteredUsers() {
        UserResponseDto user = new UserResponseDto();
        user.setId(2L);
        user.setRole("EMPLOYEE");
        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        User employee = new User();
        employee.setId(2L);
        employee.setRole(UserRole.EMPLOYEE);
        when(userRepository.findByRole(UserRole.EMPLOYEE)).thenReturn(List.of(employee));

        StandardResponseDto<List<UserResponseDto>> response = userController.getUsersByRole(UserRole.EMPLOYEE, "1");

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }

    @Test
    void changePasswordShouldThrowWhenHeaderAndPathUserMismatch() {
        ChangePasswordRequestDto req = new ChangePasswordRequestDto("old", "new");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userController.changePassword(5L, req, "10"));

        assertEquals("You can only change your own password", exception.getMessage());
    }
}
