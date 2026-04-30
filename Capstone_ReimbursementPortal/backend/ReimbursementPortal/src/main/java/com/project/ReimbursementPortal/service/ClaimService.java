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
import java.util.stream.Collectors;


@Service
public final class ClaimService {

    /** Claim persistence operations. */
    private final ClaimRepository claimRepository;

    /** User persistence operations. */
    private final UserRepository userRepository;

    /** User operations used for reviewer fallback. */
    private final UserService userService;

    /** Maximum allowed claim amount (configurable via application properties). */
    @Value("${app.claim.max-amount:${app.max-claim-amount:50000}}")
    private Double maxClaimAmount;

    /**
     * Creates a claim service.
     *
     * @param claimRepository claim repository
     * @param userRepository user repository
     * @param userService user service
     */
    public ClaimService(final ClaimRepository claimRepository,
                        final UserRepository userRepository,
                        final UserService userService) {
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Submits a new claim for the current employee.
     *
     * @param req request payload
     * @param currentUserId current user id
     * @return submitted claim
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
     * Returns a claim by id if the current user is authorized to view it.
     *
     * @param claimId claim id
     * @param currentUserId current user id
     * @return claim
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
            if (!claim.getReviewerId().equals(currentUserId)) {
                throw new ForbiddenException("You are not authorized to view this claim");
            }
            return ClaimMapper.toDTO(claim);
        }

        throw new ForbiddenException("Not authorized");
    }

    /**
     * Edits a rejected claim and resubmits it.
     *
     * @param claimId claim id
     * @param req request payload
     * @param currentUserId current user id
     * @return updated claim
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
     * Returns all claims submitted by the current employee.
     *
     * @param currentUserId current user id
     * @return list of claims
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
     * Returns paginated claims submitted by the current employee.
     *
     * @param currentUserId current user id
     * @param pageable pageable
     * @return page of claims
     */
    public Page<ClaimResponseDto> getMyClaimsPaginated(final Long currentUserId, final Pageable pageable) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.EMPLOYEE) {
            throw new ForbiddenException("Only EMPLOYEE can view their claims");
        }

        return claimRepository.findByEmployeeId(currentUserId, pageable)
                .map(ClaimMapper::toDTO);
    }

    /**
     * Returns claims assigned to the current reviewer (manager/admin).
     *
     * @param currentUserId current user id
     * @return list of claims
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
     * Returns paginated claims assigned to the current reviewer (manager/admin).
     *
     * @param currentUserId current user id
     * @param pageable pageable
     * @return page of claims
     */
    public Page<ClaimResponseDto> getClaimsForReviewerPaginated(final Long currentUserId, final Pageable pageable) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.MANAGER
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Not authorized");
        }

        return claimRepository.findByReviewerId(currentUserId, pageable)
                .map(ClaimMapper::toDTO);
    }

    /**
     * Approves a submitted claim assigned to the current reviewer.
     *
     * @param claimId claim id
     * @param currentUserId current user id
     * @param comments optional comments
     * @return updated claim
     */
    public ClaimResponseDto approveClaim(final Long claimId, final Long currentUserId, final String comments) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.MANAGER
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Not authorized to approve");
        }

        Claim claim = getClaim(claimId);

        if (!claim.getReviewerId().equals(currentUserId)) {
            throw new ForbiddenException("You are not assigned to this claim");
        }

        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new BadRequestException("Claim already processed");
        }

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setComments(comments);

        return ClaimMapper.toDTO(claimRepository.save(claim));
    }

    /**
     * Rejects a submitted claim assigned to the current reviewer.
     *
     * @param claimId claim id
     * @param currentUserId current user id
     * @param comments optional comments
     * @return updated claim
     */
    public ClaimResponseDto rejectClaim(final Long claimId, final Long currentUserId, final String comments) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.MANAGER
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Not authorized to reject");
        }

        Claim claim = getClaim(claimId);

        if (!claim.getReviewerId().equals(currentUserId)) {
            throw new ForbiddenException("You are not assigned to this claim");
        }

        if (claim.getStatus() != ClaimStatus.SUBMITTED) {
            throw new BadRequestException("Claim already processed");
        }

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setComments(comments);

        return ClaimMapper.toDTO(claimRepository.save(claim));
    }

    /**
     * Returns all claims (ADMIN only).
     *
     * @param currentUserId current user id
     * @return list of claims
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
     * Returns all claims paginated (ADMIN only).
     *
     * @param currentUserId current user id
     * @param pageable pageable
     * @return page of claims
     */
    public Page<ClaimResponseDto> getAllClaimsPaginated(final Long currentUserId, final Pageable pageable) {

        User currentUser = getUser(currentUserId);

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new ForbiddenException("Only ADMIN can view all claims");
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
}

