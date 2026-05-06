package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.ChangePasswordRequestDto;
import com.project.ReimbursementPortal.dto.UserRequestDto;
import com.project.ReimbursementPortal.dto.UserResponseDto;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.repository.UserRepository;
import com.project.ReimbursementPortal.validator.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailValidator emailValidator;

    @InjectMocks
    private UserService userService;

    private User adminUser() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);
        user.setPassword("encoded-old");
        return user;
    }

    private User employeeUser() {
        User user = new User();
        user.setId(2L);
        user.setRole(UserRole.EMPLOYEE);
        user.setPassword("encoded-old");
        return user;
    }

    @Test
    void createUser() {
        UserRequestDto req = new UserRequestDto();
        req.setName("New Employee");
        req.setEmail("new.employee@company.com");
        req.setPassword("plain-password");
        req.setRole("EMPLOYEE");
        req.setManagerId(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser()));
        when(userRepository.findByEmail("new.employee@company.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");

        User saved = new User();
        saved.setId(10L);
        saved.setName("New Employee");
        saved.setEmail("new.employee@company.com");
        saved.setPassword("encoded-password");
        saved.setRole(UserRole.EMPLOYEE);
        saved.setManagerId(null);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponseDto response = userService.createUser(req, 1L);

        assertEquals(10L, response.getId());
        assertEquals("new.employee@company.com", response.getEmail());
        assertEquals("EMPLOYEE", response.getRole());
        verify(emailValidator).validate("new.employee@company.com");
        verify(passwordEncoder).encode("plain-password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsers() {
        User employee = employeeUser();
        employee.setName("Employee One");
        employee.setEmail("employee@company.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser()));
        when(userRepository.findAll()).thenReturn(List.of(employee));

        List<UserResponseDto> result = userService.getAllUsers(1L);

        assertEquals(1, result.size());
        assertEquals("employee@company.com", result.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void getUsersByRole() {
        User employee = employeeUser();
        employee.setName("Employee One");
        employee.setEmail("employee@company.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser()));
        when(userRepository.findByRole(UserRole.EMPLOYEE)).thenReturn(List.of(employee));

        List<UserResponseDto> result = userService.getUsersByRole(UserRole.EMPLOYEE, 1L);

        assertEquals(1, result.size());
        assertEquals("EMPLOYEE", result.get(0).getRole());
        verify(userRepository).findByRole(UserRole.EMPLOYEE);
    }

    @Test
    void assignManager() {
        User employee = employeeUser();
        employee.setManagerId(null);

        User manager = new User();
        manager.setId(3L);
        manager.setRole(UserRole.MANAGER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto result = userService.assignManager(2L, 3L, 1L);

        assertEquals(3L, result.getManagerId());
        verify(userRepository).save(employee);
    }

    @Test
    void deleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser()));
        when(userRepository.existsById(2L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(2L);

        userService.deleteUser(2L, 1L);

        verify(userRepository).deleteById(2L);
    }

    @Test
    void getUsersUnderManager() {
        User directReport = employeeUser();
        directReport.setManagerId(3L);
        directReport.setEmail("employee@company.com");

        User manager = new User();
        manager.setId(3L);
        manager.setRole(UserRole.MANAGER);

        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(userRepository.findByManagerId(3L)).thenReturn(List.of(directReport));

        List<UserResponseDto> result = userService.getUsersUnderManager(3L, 3L);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getManagerId());
        verify(userRepository).findByManagerId(3L);
    }

    @Test
    void getAnyAdminId() {
        User admin = adminUser();
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(admin));

        Long adminId = userService.getAnyAdminId();

        assertEquals(1L, adminId);
    }

    @Test
    void changePassword() {
        ChangePasswordRequestDto req = new ChangePasswordRequestDto("old-password", "new-password");

        User currentUser = adminUser();
        currentUser.setName("Admin");
        currentUser.setEmail("admin@company.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "encoded-old")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDto response = userService.changePassword(1L, req);

        assertEquals(1L, response.getId());
        verify(passwordEncoder).encode("new-password");
        verify(userRepository).save(currentUser);
    }

    @Test
    void changePasswordShouldThrowWhenCurrentPasswordIsWrong() {
        ChangePasswordRequestDto req = new ChangePasswordRequestDto("wrong", "new-password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser()));
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.changePassword(1L, req));

        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}