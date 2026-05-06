package com.project.ReimbursementPortal.mapper;

import com.project.ReimbursementPortal.dto.ClaimRequestDto;
import com.project.ReimbursementPortal.dto.ClaimResponseDto;
import com.project.ReimbursementPortal.entity.Claim;
import com.project.ReimbursementPortal.enums.ClaimStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClaimMapperTest {

    @Test
    void toEntityShouldMapRequestToEntity() {
        ClaimRequestDto req = new ClaimRequestDto();
        req.setAmount(250.0);
        req.setTitle("Travel");
        req.setDescription("Airport taxi");
        req.setEmployeeId(2L);

        Claim claim = ClaimMapper.toEntity(req);

        assertEquals(250.0, claim.getAmount());
        assertEquals("Travel", claim.getTitle());
        assertEquals("Airport taxi", claim.getDescription());
        assertEquals(2L, claim.getEmployeeId());
    }

    @Test
    void toDTOShouldMapEntityToResponse() {
        Claim claim = new Claim();
        claim.setId(7L);
        claim.setAmount(180.0);
        claim.setTitle("Meal");
        claim.setDescription("Client lunch");
        claim.setDate(LocalDate.of(2026, 1, 5));
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setEmployeeId(2L);
        claim.setReviewerId(3L);
        claim.setComments("Approved");

        ClaimResponseDto dto = ClaimMapper.toDTO(claim);

        assertEquals(7L, dto.getId());
        assertEquals(180.0, dto.getAmount());
        assertEquals("Meal", dto.getTitle());
        assertEquals("Client lunch", dto.getDescription());
        assertEquals(LocalDate.of(2026, 1, 5), dto.getDate());
        assertEquals("APPROVED", dto.getStatus());
        assertEquals(2L, dto.getEmployeeId());
        assertEquals(3L, dto.getReviewerId());
        assertEquals("Approved", dto.getComments());
    }
}
