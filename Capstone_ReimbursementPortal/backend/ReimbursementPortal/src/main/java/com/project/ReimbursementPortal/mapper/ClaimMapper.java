package com.project.ReimbursementPortal.mapper;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.entity.Claim;

public final class ClaimMapper {

    /** static helpers only. */
    private ClaimMapper() {
    }

    /**
     * @param req inbound fields (IDs/status still set elsewhere)
     * @return Hibernate-managed instance before save()
     */
    public static Claim toEntity(final ClaimRequestDto req) {
        Claim claim = new Claim();
        claim.setAmount(req.getAmount());
        claim.setTitle(req.getTitle());
        claim.setDescription(req.getDescription());
        claim.setEmployeeId(req.getEmployeeId());
        return claim;
    }

    /**
     * @param claim attached row
     * @return wire-friendly JSON dto (amount/dates as primitives / strings downstream)
     */
    public static ClaimResponseDto toDTO(final Claim claim) {
        ClaimResponseDto res = new ClaimResponseDto();
        res.setId(claim.getId());
        res.setAmount(claim.getAmount());
        res.setTitle(claim.getTitle());
        res.setDescription(claim.getDescription());
        res.setDate(claim.getDate());
        res.setStatus(claim.getStatus().name());
        res.setEmployeeId(claim.getEmployeeId());
        res.setReviewerId(claim.getReviewerId());
        res.setComments(claim.getComments());
        return res;
    }
}
