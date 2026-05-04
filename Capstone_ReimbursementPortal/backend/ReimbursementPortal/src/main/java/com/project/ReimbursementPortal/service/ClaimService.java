package com.project.ReimbursementPortal.service;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.entity.Claim;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.ClaimStatus;
import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.exception.ClaimNotFoundException;
import com.project.ReimbursementPortal.exception.ForbiddenException;
import com.project.ReimbursementPortal.exception.UserNotFoundException;
import com.project.ReimbursementPortal.mapper.ClaimMapper;
import com.project.ReimbursementPortal.repository.ClaimRepository;
import com.project.ReimbursementPortal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public final class ClaimService {

    /**
     * Repository for accessing claim data.
     */
    private final ClaimRepository claimRepository;

    /**
     * Repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Service for user-related operations.
     */
    private final UserService userService;

    /**
     * Optional maximum claim amount configured via application properties.
     */
    @Value("${app.max-claim-amount}")
    private Double maxClaimAmount;

    /**
     * @param claimRepository repo
     * @param userRepository repo
     * @param userService need admin reviewer fallback row
     */
    public ClaimService(final ClaimRepository claimRepository,
                        final UserRepository userRepository,
                        final UserService userService) {
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * @param req POST body; employeeId must equal logged-in employee
     * @param currentUserId from X-USER-ID header
     * @return persisted DTO
     */
    public ClaimResponseDto submitClaim(final ClaimRequestDto req, final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.EMPLOYEE) {
            throw new ForbiddenException("Only EMPLOYEE can submit claims");
        }

        if (!currentUser.getId().equals(req.getEmployeeId())) {
            throw new ForbiddenException("You can only submit your own claims");
        }

        validateAmount(req.getAmount());

        Long reviewerId = (currentUser.getManagerId() != null)
                ? currentUser.getManagerId()
                : userService.getAnyAdminId();

        Claim claim = ClaimMapper.toEntity(req);
        claim.setReviewerId(reviewerId);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setDate(resolveAndValidateDate(req.getDate()));

        Claim saved = claimRepository.save(claim);

        return ClaimMapper.toDTO(saved);
    }

    /**
     * @param claimId row id
     * @param currentUserId caller
     * @return dto if EMPLOYEE self / MANAGER assigned reviewer / ADMIN
     */
    public ClaimResponseDto getClaimById(final Long claimId, final Long currentUserId) {

        User currentUser = getUser(currentUserId);
        Claim claim = getClaim(claimId);

        if (currentUser.getRole() == UserRole.ADMIN) {
            return ClaimMapper.toDTO(claim);
        }

        if (currentUser.getRole() == UserRole.EMPLOYEE) {
            if (!claim.getEmployeeId().equals(currentUserId)) {
                throw new ForbiddenException("You are not authorized to view this claim");
            }
            return ClaimMapper.toDTO(claim);
        }

        if (currentUser.getRole() == UserRole.MANAGER) {
            if (!Objects.equals(claim.getReviewerId(), currentUserId)) {
                throw new ForbiddenException("You are not authorized to view this claim");
            }
            return ClaimMapper.toDTO(claim);
        }

        throw new ForbiddenException("Not authorized");
    }

    /**
     * Only REJECTED rows, owner EMPLOYEE.
     *
     * @param claimId row
     * @param req patched fields
     * @param currentUserId caller
     * @return reopened SUBMITTED claim
     */
    public ClaimResponseDto editAndResubmitClaim(final Long claimId,
                                                 final ClaimRequestDto req,
                                                 final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.EMPLOYEE) {
            throw new ForbiddenException("Only EMPLOYEE can edit and resubmit claims");
        }

        if (!currentUser.getId().equals(req.getEmployeeId())) {
            throw new ForbiddenException("You can only edit and resubmit your own claims");
        }

        Claim claim = getClaim(claimId);

        if (!claim.getEmployeeId().equals(currentUserId)) {
            throw new ForbiddenException("You are not allowed to edit this claim");
        }

        if (claim.getStatus() != ClaimStatus.REJECTED) {
            throw new BadRequestException("Only rejected claims can be edited and resubmitted");
        }

        validateAmount(req.getAmount());

        claim.setAmount(req.getAmount());
        claim.setDescription(req.getDescription());
        claim.setDate(resolveAndValidateDate(req.getDate()));
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setComments(null);

        Claim saved = claimRepository.save(claim);

        return ClaimMapper.toDTO(saved);
    }

    /**
     * EMPLOYEE only.
     *
     * @param currentUserId caller
     * @return non-paged mine list
     */
    public List<ClaimResponseDto> getMyClaims(final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.EMPLOYEE) {
            throw new ForbiddenException("Only EMPLOYEE can view their claims");
        }

        return claimRepository.findByEmployeeId(currentUserId)
                .stream()
                .map(ClaimMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * EMPLOYEE + Spring page request.
     *
     * @param currentUserId caller
     * @param pageable page/size/sort
     * @param status filter or null (= all mine)
     * @return page dto slice
     */
    public Page<ClaimResponseDto> getMyClaimsPaginated(final Long currentUserId,
                                                       final Pageable pageable,
                                                       final ClaimStatus status) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.EMPLOYEE) {
            throw new ForbiddenException("Only EMPLOYEE can view their claims");
        }

        if (status != null) {
            return claimRepository.findByEmployeeIdAndStatus(currentUserId, status, pageable)
                    .map(ClaimMapper::toDTO);
        }
        return claimRepository.findByEmployeeId(currentUserId, pageable)
                .map(ClaimMapper::toDTO);
    }

    /**
     * MANAGER/ADMIN reviewer id == my user id column.
     *
     * @param currentUserId caller
     * @return reviewer queue flat list
     */
    public List<ClaimResponseDto> getClaimsForReviewer(final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.MANAGER
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Not authorized");
        }

        return claimRepository.findByReviewerId(currentUserId)
                .stream()
                .map(ClaimMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Same reviewer rule as list endpoint, paged + optional SUBMITTED/… filter via repo.
     *
     * @param currentUserId caller
     * @param pageable page/size/sort
     * @param status optional filter null=all assigned
     * @return reviewer page
     */
    public Page<ClaimResponseDto> getClaimsForReviewerPaginated(final Long currentUserId,
                                                                final Pageable pageable,
                                                                final ClaimStatus status) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.MANAGER
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Not authorized");
        }

        if (status != null) {
            return claimRepository.findByReviewerIdAndStatus(currentUserId, status, pageable)
                    .map(ClaimMapper::toDTO);
        }
        return claimRepository.findByReviewerId(currentUserId, pageable)
                .map(ClaimMapper::toDTO);
    }

    /**
     * Claim must stay SUBMITTED; MANAGER must match reviewerId; ADMIN can override queue.
     *
     * @param claimId row
     * @param currentUserId reviewer
     * @param comments optional reviewer text
     * @return finalized row dto
     */
    public ClaimResponseDto approveClaim(final Long claimId, final Long currentUserId, final String comments) {

        User currentUser = getUser(currentUserId);

        if (!(currentUser.getRole() == UserRole.MANAGER
                || currentUser.getRole() == UserRole.ADMIN)) {
            throw new ForbiddenException("Not authorized to approve");
        }

        Claim claim = getClaim(claimId);

        if (currentUser.getRole() == UserRole.MANAGER) {
            if (!Objects.equals(claim.getReviewerId(), currentUserId)) {
                throw new ForbiddenException("You are not assigned to this claim");
            }
        } else {
            claim.setReviewerId(currentUserId);
        }

        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new BadRequestException("Claim already processed");
        }

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setComments(comments);

        return ClaimMapper.toDTO(claimRepository.save(claim));
    }

    /**
     * Same guard rails as approve, sets REJECTED.
     *
     * @param claimId row
     * @param currentUserId reviewer
     * @param comments optional reviewer text
     * @return updated dto
     */
    public ClaimResponseDto rejectClaim(final Long claimId, final Long currentUserId, final String comments) {

        User currentUser = getUser(currentUserId);

        if (!(currentUser.getRole() == UserRole.MANAGER
                || currentUser.getRole() == UserRole.ADMIN)) {
            throw new ForbiddenException("Not authorized to reject");
        }

        Claim claim = getClaim(claimId);

        if (currentUser.getRole() == UserRole.MANAGER) {
            if (!Objects.equals(claim.getReviewerId(), currentUserId)) {
                throw new ForbiddenException("You are not assigned to this claim");
            }
        } else {
            claim.setReviewerId(currentUserId);
        }

        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new BadRequestException("Claim already processed");
        }

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setComments(comments);

        return ClaimMapper.toDTO(claimRepository.save(claim));
    }

    /**
     * @param currentUserId must be ADMIN
     * @return everything (careful perf on big DB)
     */
    public List<ClaimResponseDto> getAllClaims(final Long currentUserId) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can view all claims");
        }

        return claimRepository.findAll()
                .stream()
                .map(ClaimMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * ADMIN table scan with optional status narrowing.
     *
     * @param currentUserId admin id
     * @param pageable paging
     * @param status null = all statuses
     * @return page
     */
    public Page<ClaimResponseDto> getAllClaimsPaginated(final Long currentUserId,
                                                        final Pageable pageable,
                                                        final ClaimStatus status) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can view all claims");
        }

        if (status != null) {
            return claimRepository.findByStatus(status, pageable)
                    .map(ClaimMapper::toDTO);
        }
        return claimRepository.findAll(pageable)
                .map(ClaimMapper::toDTO);
    }

    private User getUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Claim getClaim(final Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException("Claim not found"));
    }

    private void validateAmount(final Double amount) {
        if (amount == null) {
            throw new BadRequestException("Amount is required");
        }
        if (amount.isNaN() || amount.isInfinite() || amount <= 0) {
            throw new BadRequestException("Amount must be a finite number greater than 0");
        }
        if (maxClaimAmount != null && amount > maxClaimAmount) {
            throw new BadRequestException("Claim amount exceeds maximum limit of " + maxClaimAmount);
        }
    }

    private LocalDate resolveAndValidateDate(final LocalDate dateFromClient) {
        LocalDate resolved = (dateFromClient != null) ? dateFromClient : LocalDate.now();
        if (resolved.isAfter(LocalDate.now())) {
            throw new BadRequestException("Date cannot be in the future");
        }
        return resolved;
    }

    /**
     * ADMIN + SUBMITTED only (avoid deleting history accidentally).
     *
     * @param claimId target id
     * @param userId acting admin header id
     */
    public void deleteClaim(final Long claimId, final Long userId) {

        User currentUser = getUser(userId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can delete claims");
        }

        Claim claim = getClaim(claimId);

        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new BadRequestException("Only claims in SUBMITTED status can be deleted");
        }

        claimRepository.delete(claim);
    }
}

