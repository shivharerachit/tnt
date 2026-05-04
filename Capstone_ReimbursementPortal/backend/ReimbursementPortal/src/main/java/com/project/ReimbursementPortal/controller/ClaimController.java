package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.enums.ClaimStatus;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.service.ClaimService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimController.class);

    /**
     * Service for handling claim-related business logic.
     */
    private final ClaimService claimService;

    /**
     * @param claimService service
     */
    public ClaimController(final ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * @param userIdHeader X-USER-ID
     * @return user id
     * @throws BadRequestException if not numeric
     */
    private Long getUserId(final String userIdHeader) {
        try {
            return Long.parseLong(userIdHeader);
        } catch (Exception e) {
            throw new BadRequestException("Invalid X-USER-ID header: must be a valid number");
        }
    }

    /**
     * Optional status filter (query name {@code claimStatus} so it does not collide with {@code sort}).
     *
     * @param raw query value or blank
     * @return parsed status or null
     */
    private ClaimStatus parseClaimStatusFilter(final String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return ClaimStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid claimStatus. Use SUBMITTED, APPROVED, or REJECTED.");
        }
    }

    /**
     * @param req body
     * @param userIdHeader X-USER-ID
     * @return created claim
     */
    @PostMapping
    public StandardResponseDto<ClaimResponseDto> submitClaim(
            final @Valid @RequestBody ClaimRequestDto req,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("POST /claims submit callerUserId={}", userId);

        ClaimResponseDto res = claimService.submitClaim(req, userId);

        LOGGER.info("POST /claims success callerUserId={} claimId={}", userId, res.getId());

        return new StandardResponseDto<>(true, "Claim submitted successfully", res);
    }

    /**
     * @param userIdHeader X-USER-ID
     * @return employee's claims
     */
    @GetMapping("/user-claims")
    public StandardResponseDto<List<ClaimResponseDto>> getMyClaims(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("GET /claims/user-claims callerUserId={}", userId);

        return new StandardResponseDto<>(
                true,
                "My claims fetched",
                claimService.getMyClaims(userId)
        );
    }

    /**
     * @param userIdHeader X-USER-ID
     * @param pageable paging
     * @param claimStatusFilter optional SUBMITTED / APPROVED / REJECTED
     * @return page of my claims
     */
    @GetMapping("/my/paginated")
    public StandardResponseDto<Page<ClaimResponseDto>> getMyClaimsPaginated(
            final @RequestHeader("X-USER-ID") String userIdHeader,
            final Pageable pageable,
            final @RequestParam(value = "claimStatus", required = false) String claimStatusFilter) {

        Long userId = getUserId(userIdHeader);
        ClaimStatus status = parseClaimStatusFilter(claimStatusFilter);

        LOGGER.info(
                "GET /claims/my/paginated callerUserId={} claimStatusFilter={} page={} size={}",
                userId,
                status,
                pageable.getPageNumber(),
                pageable.getPageSize());

        return new StandardResponseDto<>(
                true,
                "My claims fetched (paginated)",
                claimService.getMyClaimsPaginated(userId, pageable, status)
        );
    }

    /**
     * @param userIdHeader X-USER-ID
     * @return claims I should review
     */
    @GetMapping("/reviewer")
    public StandardResponseDto<List<ClaimResponseDto>> getClaimsForReviewer(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("GET /claims/reviewer callerUserId={}", userId);

        return new StandardResponseDto<>(
                true,
                "Reviewer claims fetched",
                claimService.getClaimsForReviewer(userId)
        );
    }

    /**
     * @param userIdHeader X-USER-ID
     * @param pageable paging
     * @param claimStatusFilter optional status
     * @return page for reviewer queue
     */
    @GetMapping("/reviewer/paginated")
    public StandardResponseDto<Page<ClaimResponseDto>> getClaimsForReviewerPaginated(
            final @RequestHeader("X-USER-ID") String userIdHeader,
            final Pageable pageable,
            final @RequestParam(value = "claimStatus", required = false) String claimStatusFilter) {

        Long userId = getUserId(userIdHeader);
        ClaimStatus status = parseClaimStatusFilter(claimStatusFilter);

        LOGGER.info(
                "GET /claims/reviewer/paginated callerUserId={} claimStatusFilter={} page={} size={}",
                userId,
                status,
                pageable.getPageNumber(),
                pageable.getPageSize());

        return new StandardResponseDto<>(
                true,
                "Reviewer claims fetched (paginated)",
                claimService.getClaimsForReviewerPaginated(userId, pageable, status)
        );
    }

    /**
     * @param claimId claim id
     * @param comments optional reviewer note
     * @param userIdHeader X-USER-ID (reviewer)
     * @return updated claim
     */
    @PutMapping("/{claimId}/approve")
    public StandardResponseDto<ClaimResponseDto> approveClaim(
            final @PathVariable Long claimId,
            final @RequestParam(required = false) String comments,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        ClaimResponseDto res = claimService.approveClaim(claimId, userId, comments);

        LOGGER.info("PUT /claims/{}/approve success reviewerUserId={}", claimId, userId);

        return new StandardResponseDto<>(true, "Claim approved", res);
    }

    /**
     * @param claimId claim id
     * @param comments optional reviewer note
     * @param userIdHeader X-USER-ID (reviewer)
     * @return updated claim
     */
    @PutMapping("/{claimId}/reject")
    public StandardResponseDto<ClaimResponseDto> rejectClaim(
            final @PathVariable Long claimId,
            final @RequestParam(required = false) String comments,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("PUT /claims/{}/reject reviewerUserId={}", claimId, userId);

        ClaimResponseDto res = claimService.rejectClaim(claimId, userId, comments);

        LOGGER.info("PUT /claims/{}/reject success reviewerUserId={}", claimId, userId);

        return new StandardResponseDto<>(true, "Claim rejected", res);
    }

    /**
     * @param claimId claim id
     * @param req updates
     * @param userIdHeader X-USER-ID
     * @return resubmitted claim
     */
    @PutMapping("/{claimId}")
    public StandardResponseDto<ClaimResponseDto> editAndResubmitClaim(
            final @PathVariable Long claimId,
            final @Valid @RequestBody ClaimRequestDto req,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("PUT /claims/{} resubmit callerUserId={}", claimId, userId);

        ClaimResponseDto res = claimService.editAndResubmitClaim(claimId, req, userId);

        LOGGER.info("PUT /claims/{} resubmit success callerUserId={}", claimId, userId);

        return new StandardResponseDto<>(true, "Claim resubmitted successfully", res);
    }

    /**
     * @param claimId claim id
     * @param userIdHeader X-USER-ID
     * @return claim if caller may see it
     */
    @GetMapping("/{claimId}")
    public StandardResponseDto<ClaimResponseDto> getClaimById(
            final @PathVariable Long claimId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("GET /claims/{} callerUserId={}", claimId, userId);

        return new StandardResponseDto<>(
                true,
                "Claim fetched successfully",
                claimService.getClaimById(claimId, userId)
        );
    }

    /**
     * @param userIdHeader X-USER-ID (ADMIN)
     * @return every claim
     */
    @GetMapping("/all")
    public StandardResponseDto<List<ClaimResponseDto>> getAllClaims(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("GET /claims/all callerUserId={}", userId);

        return new StandardResponseDto<>(
                true,
                "All claims fetched",
                claimService.getAllClaims(userId)
        );
    }

    /**
     * @param userIdHeader X-USER-ID (ADMIN)
     * @param pageable paging
     * @param claimStatusFilter optional status
     * @return full list page
     */
    @GetMapping("/all/paginated")
    public StandardResponseDto<Page<ClaimResponseDto>> getAllClaimsPaginated(
            final @RequestHeader("X-USER-ID") String userIdHeader,
            final Pageable pageable,
            final @RequestParam(value = "claimStatus", required = false) String claimStatusFilter) {

        Long userId = getUserId(userIdHeader);
        ClaimStatus status = parseClaimStatusFilter(claimStatusFilter);

        LOGGER.info(
                "GET /claims/all/paginated callerUserId={} claimStatusFilter={} page={} size={}",
                userId,
                status,
                pageable.getPageNumber(),
                pageable.getPageSize());

        return new StandardResponseDto<>(
                true,
                "All claims fetched (paginated)",
                claimService.getAllClaimsPaginated(userId, pageable, status)
        );
    }

    /**
     * @param claimId claim id
     * @param userIdHeader X-USER-ID (ADMIN)
     * @return empty envelope on success
     */
    @DeleteMapping("/{claimId}")
    public StandardResponseDto<Void> deleteClaim(
            final @PathVariable Long claimId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        LOGGER.info("DELETE /claims/{} callerUserId={}", claimId, userId);

        claimService.deleteClaim(claimId, userId);

        LOGGER.info("DELETE /claims/{} success callerUserId={}", claimId, userId);

        return new StandardResponseDto<>(
                true,
                "Claim deleted successfully",
                null
        );
    }
}
