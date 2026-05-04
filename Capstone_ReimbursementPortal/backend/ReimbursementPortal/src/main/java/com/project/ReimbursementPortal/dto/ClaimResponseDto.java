package com.project.ReimbursementPortal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO returned from claim APIs.
 */
@Getter
@Setter
public class ClaimResponseDto {

    /**
     * Claim identifier.
     */
    private Long id;

    /**
     * Claim amount.
     */
    private Double amount;

    /**
     * Claim title - a brief summary of the expense.
     */
    private String title;

    /**
     * Claim description - details about the expense being claimed.
     */
    private String description;

    /**
     * Expense/claim date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Claim status.
     */
    private String status;

    /**
     * Employee id who submitted the claim.
     */
    private Long employeeId;

    /**
     * Reviewer id assigned to process this claim.
     */
    private Long reviewerId;

    /**
     * Reviewer comments (approval/rejection reason).
     */
    private String comments;
}
