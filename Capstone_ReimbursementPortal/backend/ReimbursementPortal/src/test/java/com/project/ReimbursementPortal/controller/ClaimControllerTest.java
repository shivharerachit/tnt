package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.entity.Claim;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.ClaimStatus;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.repository.ClaimRepository;
import com.project.ReimbursementPortal.repository.UserRepository;
import com.project.ReimbursementPortal.service.ClaimService;
import com.project.ReimbursementPortal.service.UserService;
import com.project.ReimbursementPortal.validator.EmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimControllerTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailValidator emailValidator;

    private ClaimService claimService;
    private ClaimController claimController;

    @BeforeEach
    void setup() {
        UserService userService = new UserService(userRepository, passwordEncoder, emailValidator);
        claimService = new ClaimService(claimRepository, userRepository, userService);
        ReflectionTestUtils.setField(claimService, "maxClaimAmount", 1000.0);
        claimController = new ClaimController(claimService);
    }

    @Test
    void submitClaimShouldReturnSuccessResponse() {
        ClaimRequestDto req = new ClaimRequestDto();
        req.setEmployeeId(2L);
        req.setAmount(100.0);
        req.setTitle("Travel");
        req.setDescription("Taxi reimbursement");
        req.setDate(LocalDate.now());

        User employee = new User();
        employee.setId(2L);
        employee.setRole(UserRole.EMPLOYEE);
        employee.setManagerId(3L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));

        when(claimRepository.save(org.mockito.ArgumentMatchers.any(Claim.class))).thenAnswer(invocation -> {
            Claim claim = invocation.getArgument(0);
            claim.setId(11L);
            return claim;
        });

        StandardResponseDto<ClaimResponseDto> response = claimController.submitClaim(req, "2");

        assertTrue(response.isSuccess());
        assertEquals("Claim submitted successfully", response.getMessage());
        assertEquals(11L, response.getData().getId());
    }

    @Test
    void getMyClaimsPaginatedShouldThrowForInvalidStatusFilter() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> claimController.getMyClaimsPaginated("2", PageRequest.of(0, 5), "INVALID"));

        assertEquals("Invalid claimStatus. Use SUBMITTED, APPROVED, or REJECTED.", exception.getMessage());
    }

    @Test
    void getAllClaimsShouldReturnList() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        Claim claim = new Claim();
        claim.setId(10L);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setDate(LocalDate.now());
        when(claimRepository.findAll()).thenReturn(List.of(claim));

        StandardResponseDto<List<ClaimResponseDto>> response = claimController.getAllClaims("1");

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
    }
}
