package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.entity.Claim;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.ClaimStatus;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.repository.ClaimRepository;
import com.project.ReimbursementPortal.repository.UserRepository;
import com.project.ReimbursementPortal.validator.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailValidator emailValidator;

    private ClaimService claimService;

    @BeforeEach
    void setup() {
        UserService userService = new UserService(userRepository, passwordEncoder, emailValidator);
        claimService = new ClaimService(claimRepository, userRepository, userService);
    }

    private User user(Long id, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private Claim claim(Long id, Long employeeId, Long reviewerId, ClaimStatus status) {
        Claim claim = new Claim();
        claim.setId(id);
        claim.setEmployeeId(employeeId);
        claim.setReviewerId(reviewerId);
        claim.setStatus(status);
        claim.setAmount(100.0);
        claim.setTitle("Travel");
        claim.setDescription("Taxi reimbursement");
        claim.setDate(LocalDate.now());
        return claim;
    }

    @Test
    void submitClaim() {
        ReflectionTestUtils.setField(claimService, "maxClaimAmount", 1000.0);
        ClaimRequestDto req = new ClaimRequestDto();
        req.setEmployeeId(2L);
        req.setAmount(100.0);
        req.setTitle("Travel");
        req.setDescription("Taxi reimbursement");
        req.setDate(LocalDate.now());

        User employee = user(2L, UserRole.EMPLOYEE);
        employee.setManagerId(3L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> {
            Claim c = invocation.getArgument(0);
            c.setId(11L);
            return c;
        });

        ClaimResponseDto response = claimService.submitClaim(req, 2L);

        assertEquals(11L, response.getId());
        assertEquals("SUBMITTED", response.getStatus());
        assertEquals(3L, response.getReviewerId());
        verify(claimRepository).save(any(Claim.class));
    }

    @Test
    void getClaimById() {
        User admin = user(1L, UserRole.ADMIN);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));

        ClaimResponseDto response = claimService.getClaimById(10L, 1L);

        assertEquals(10L, response.getId());
        assertEquals("SUBMITTED", response.getStatus());
    }


    @Test
    void getMyClaims() {
        User employee = user(2L, UserRole.EMPLOYEE);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(claimRepository.findByEmployeeId(2L)).thenReturn(List.of(claim));

        List<ClaimResponseDto> result = claimService.getMyClaims(2L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void getMyClaimsPaginated() {
        User employee = user(2L, UserRole.EMPLOYEE);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Claim> page = new PageImpl<>(List.of(claim));

        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(claimRepository.findByEmployeeIdAndStatus(2L, ClaimStatus.SUBMITTED, pageable)).thenReturn(page);

        Page<ClaimResponseDto> result = claimService.getMyClaimsPaginated(2L, pageable, ClaimStatus.SUBMITTED);

        assertEquals(1, result.getTotalElements());
        verify(claimRepository).findByEmployeeIdAndStatus(2L, ClaimStatus.SUBMITTED, pageable);
    }

    @Test
    void getClaimsForReviewer() {
        User manager = user(3L, UserRole.MANAGER);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(claimRepository.findByReviewerId(3L)).thenReturn(List.of(claim));

        List<ClaimResponseDto> result = claimService.getClaimsForReviewer(3L);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getReviewerId());
    }

    @Test
    void getClaimsForReviewerPaginated() {
        User manager = user(3L, UserRole.MANAGER);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Claim> page = new PageImpl<>(List.of(claim));
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(claimRepository.findByReviewerId(3L, pageable)).thenReturn(page);

        Page<ClaimResponseDto> result = claimService.getClaimsForReviewerPaginated(3L, pageable, null);

        assertEquals(1, result.getTotalElements());
        verify(claimRepository).findByReviewerId(3L, pageable);
    }

    @Test
    void approveClaim() {
        User manager = user(3L, UserRole.MANAGER);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClaimResponseDto response = claimService.approveClaim(10L, 3L, "Looks good");

        assertEquals("APPROVED", response.getStatus());
        assertEquals("Looks good", response.getComments());
    }

    @Test
    void rejectClaim() {
        User manager = user(3L, UserRole.MANAGER);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        when(userRepository.findById(3L)).thenReturn(Optional.of(manager));
        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClaimResponseDto response = claimService.rejectClaim(10L, 3L, "Need receipt");

        assertEquals("REJECTED", response.getStatus());
        assertEquals("Need receipt", response.getComments());
    }

    @Test
    void getAllClaims() {
        User admin = user(1L, UserRole.ADMIN);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(claimRepository.findAll()).thenReturn(List.of(claim));

        List<ClaimResponseDto> result = claimService.getAllClaims(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void getAllClaimsPaginated() {
        User admin = user(1L, UserRole.ADMIN);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Claim> page = new PageImpl<>(List.of(claim));

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(claimRepository.findAll(pageable)).thenReturn(page);

        Page<ClaimResponseDto> result = claimService.getAllClaimsPaginated(1L, pageable, null);

        assertEquals(1, result.getTotalElements());
        verify(claimRepository).findAll(pageable);
    }

    @Test
    void deleteClaim() {
        User admin = user(1L, UserRole.ADMIN);
        Claim claim = claim(10L, 2L, 3L, ClaimStatus.SUBMITTED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));
        doNothing().when(claimRepository).delete(claim);

        claimService.deleteClaim(10L, 1L);

        verify(claimRepository).delete(claim);
    }

    @Test
    void submitClaimShouldThrowWhenAmountExceedsConfiguredLimit() {
        ReflectionTestUtils.setField(claimService, "maxClaimAmount", 100.0);
        ClaimRequestDto req = new ClaimRequestDto();
        req.setEmployeeId(2L);
        req.setAmount(500.0);
        req.setTitle("Travel");
        req.setDescription("Taxi reimbursement");
        req.setDate(LocalDate.now());

        User employee = user(2L, UserRole.EMPLOYEE);
        employee.setManagerId(3L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> claimService.submitClaim(req, 2L));
        assertEquals("Claim amount exceeds maximum limit of 100.0", exception.getMessage());
    }
}