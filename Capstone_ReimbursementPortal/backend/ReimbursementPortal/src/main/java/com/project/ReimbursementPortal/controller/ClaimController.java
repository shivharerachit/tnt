package com.project.ReimbursementPortal.controller;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.dto.StandardResponseDto;
import com.project.ReimbursementPortal.exception.BadRequestException;
import com.project.ReimbursementPortal.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    /**
     * Claim service for handling claim-related business logic. Injected via constructor.
     */
    private final ClaimService claimService;

    /**
     * Creates a claim controller with the given claim service.
     * @param claimService the claim service to use for handling claim operations
     */
    public ClaimController(final ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * Extracts and validates the X-USER-ID header.
     * @param userIdHeader the X-USER-ID header value
     * @return parsed user ID
     * @throws BadRequestException if header is invalid
     */
    private Long getUserId(final String userIdHeader) {
        try {
            return Long.parseLong(userIdHeader);
        } catch (Exception e) {
            throw new BadRequestException("Invalid X-USER-ID header: must be a valid number");
        }
    }


    /**
     * Handles claim submission requests. Validates the request body and delegates claim creation to the service layer.
     * @param req the claim request containing claim details
     * @param userIdHeader the X-USER-ID header value representing the ID of the user submitting the claim
     * @return a standard response containing the created claim details if successful
     */
    @PostMapping
    public StandardResponseDto<ClaimResponseDto> submitClaim(
            final @Valid @RequestBody ClaimRequestDto req,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        ClaimResponseDto res = claimService.submitClaim(req, userId);

        return new StandardResponseDto<>(true, "Claim submitted successfully", res);
    }

    /**
     * Handles requests to fetch claims submitted by the current user. Delegates fetching logic to the service layer.
     * @param userIdHeader the X-USER-ID header value representing the ID of the user whose claims are being fetched
     * @return a standard response containing a list of claims submitted by the user if successful
     */
    @GetMapping("/my")
    public StandardResponseDto<List<ClaimResponseDto>> getMyClaims(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "My claims fetched",
                claimService.getMyClaims(userId)
        );
    }

    /**
     * Handles requests to fetch claims submitted by the current user in a paginated format.
     * Delegates fetching logic to the service layer.
     * @param userIdHeader the X-USER-ID header value representing the ID of the user whose claims are being fetched
     * @param pageable the pagination information (page number, size, sorting) for fetching claims
     * @return a standard response containing a paginated list of claims submitted by the user if successful
     */
    @GetMapping("/my/paginated")
    public StandardResponseDto<Page<ClaimResponseDto>> getMyClaimsPaginated(
            final @RequestHeader("X-USER-ID") String userIdHeader,
            final Pageable pageable) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "My claims fetched (paginated)",
                claimService.getMyClaimsPaginated(userId, pageable)
        );
    }

    /**
     * Handles requests to fetch claims assigned to the current user for review. Delegates fetching logic to the service layer.
     * @param userIdHeader the X-USER-ID header value representing the ID of the user whose assigned claims are being fetched
     * @return a standard response containing a list of claims assigned to the user for review if successful
     */
    @GetMapping("/reviewer")
    public StandardResponseDto<List<ClaimResponseDto>> getClaimsForReviewer(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "Reviewer claims fetched",
                claimService.getClaimsForReviewer(userId)
        );
    }

    /**
     * Handles requests to fetch claims assigned to the current user for review in a paginated format.
     * Delegates fetching logic to the service layer.
     * @param userIdHeader the X-USER-ID header value representing the ID of the user whose assigned claims are being fetched
     * @param pageable the pagination information (page number, size, sorting) for fetching claims
     * @return a standard response containing a paginated list of claims assigned to the user for review if successful
     */
    @GetMapping("/reviewer/paginated")
    public StandardResponseDto<Page<ClaimResponseDto>> getClaimsForReviewerPaginated(
            final @RequestHeader("X-USER-ID") String userIdHeader,
            final Pageable pageable) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "Reviewer claims fetched (paginated)",
                claimService.getClaimsForReviewerPaginated(userId, pageable)
        );
    }

    /**
     * Handles requests to approve a claim. Validates the claim ID and delegates approval logic to the service layer.
     * @param claimId the ID of the claim to approve
     * @param comments optional comments from the reviewer when approving the claim
     * @param userIdHeader the X-USER-ID header value representing the ID of the user approving the claim
     * @return a standard response containing the updated claim details if approval is successful
     */
    @PutMapping("/{claimId}/approve")
    public StandardResponseDto<ClaimResponseDto> approveClaim(
            final @PathVariable Long claimId,
            final @RequestParam(required = false) String comments,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        ClaimResponseDto res = claimService.approveClaim(claimId, userId, comments);

        return new StandardResponseDto<>(true, "Claim approved", res);
    }

    /**
     * Handles requests to reject a claim. Validates the claim ID and delegates rejection logic to the service layer.
     * @param claimId the ID of the claim to reject
     * @param comments optional comments from the reviewer when rejecting the claim
     * @param userIdHeader the X-USER-ID header value representing the ID of the user rejecting the claim
     * @return a standard response containing the updated claim details if rejection is successful
     */
    @PutMapping("/{claimId}/reject")
    public StandardResponseDto<ClaimResponseDto> rejectClaim(
            final @PathVariable Long claimId,
            final @RequestParam(required = false) String comments,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        ClaimResponseDto res = claimService.rejectClaim(claimId, userId, comments);

        return new StandardResponseDto<>(true, "Claim rejected", res);
    }

    /**
     * Handles requests to edit and resubmit a claim. Validates the claim ID and request body,
     * then delegates editing and resubmission logic to the service layer.
     * @param claimId the ID of the claim to edit and resubmit
     * @param req the claim request containing updated claim details
     * @param userIdHeader the X-USER-ID header value representing the ID of the user editing and resubmitting the claim
     * @return a standard response containing the updated claim details if resubmission is successful
     */
    @PutMapping("/{claimId}")
    public StandardResponseDto<ClaimResponseDto> editAndResubmitClaim(
            final @PathVariable Long claimId,
            final @Valid @RequestBody ClaimRequestDto req,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        ClaimResponseDto res = claimService.editAndResubmitClaim(claimId, req, userId);

        return new StandardResponseDto<>(true, "Claim resubmitted successfully", res);
    }

    /**
     * Handles requests to fetch a claim by its ID. Validates the claim ID and delegates fetching logic to the service layer.
     * @param claimId the ID of the claim to fetch
     * @param userIdHeader the X-USER-ID header value representing the ID of the user fetching the claim
     * @return a standard response containing the claim details if found and accessible by the user
     */
    @GetMapping("/{claimId}")
    public StandardResponseDto<ClaimResponseDto> getClaimById(
            final @PathVariable Long claimId,
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "Claim fetched successfully",
                claimService.getClaimById(claimId, userId)
        );
    }

    /**
     * Handles requests to fetch all claims in the system. Delegates fetching logic to the service layer.
     * @param userIdHeader the X-USER-ID header value representing the ID of the user fetching all claims (used for authorization)
     * @return a standard response containing a list of all claims in the system if the user is authorized to view them
     */
    @GetMapping("/all")
    public StandardResponseDto<List<ClaimResponseDto>> getAllClaims(
            final @RequestHeader("X-USER-ID") String userIdHeader) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "All claims fetched",
                claimService.getAllClaims(userId)
        );
    }

    /**
     * Handles requests to fetch all claims in the system in a paginated format. Delegates fetching logic to the service layer.
     * @param userIdHeader the X-USER-ID header value representing the ID of the user fetching all claims (used for authorization)
     * @param pageable the pagination information (page number, size, sorting) for fetching claims
     * @return a standard response containing a paginated list of all claims in the system if the user is authorized to view them
     */
    @GetMapping("/all/paginated")
    public StandardResponseDto<Page<ClaimResponseDto>> getAllClaimsPaginated(
            final @RequestHeader("X-USER-ID") String userIdHeader,
            final Pageable pageable) {

        Long userId = getUserId(userIdHeader);

        return new StandardResponseDto<>(
                true,
                "All claims fetched (paginated)",
                claimService.getAllClaimsPaginated(userId, pageable)
        );
    }
}
