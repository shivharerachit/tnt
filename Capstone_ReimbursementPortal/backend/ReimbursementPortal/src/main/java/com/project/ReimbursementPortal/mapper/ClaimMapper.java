package com.project.ReimbursementPortal.mapper;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.entity.Claim;

public final class ClaimMapper {

    private ClaimMapper() {
    }

    /**
     * Maps {@link ClaimRequestDto} to {@link Claim} entity.
     * @param req the claim request DTO
     * @return the claim entity
     */
    public static Claim toEntity(final ClaimRequestDto req) {
        Claim claim = new Claim();
        claim.setAmount(req.getAmount());
        claim.setDescription(req.getDescription());
        claim.setEmployeeId(req.getEmployeeId());
        return claim;
    }

    /**
     * Maps {@link Claim} entity to {@link ClaimResponseDto}.
     * @param claim the claim entity
     * @return the claim response DTO
     */
    public static ClaimResponseDto toDTO(final Claim claim) {
        ClaimResponseDto res = new ClaimResponseDto();
        res.setId(claim.getId());
        res.setAmount(claim.getAmount());
        res.setDate(claim.getDate());
        res.setStatus(claim.getStatus().name());
        res.setEmployeeId(claim.getEmployeeId());
        res.setReviewerId(claim.getReviewerId());
        res.setComments(claim.getComments());
        return res;
    }
}
