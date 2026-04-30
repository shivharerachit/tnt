package com.project.ReimbursementPortal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for submitting a new claim.
 * Includes validation for mandatory fields and amount constraints.
 */
@Getter
@Setter
public class ClaimRequestDto {

    /**
     * Minimum description length allowed.
     */
    private static final int DESCRIPTION_MIN_LENGTH = 5;

    /**
     * Maximum description length allowed.
     */
    private static final int DESCRIPTION_MAX_LENGTH = 500;

    /**
     * Claim amount.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    /**
     * Claim description.
     */
    @NotNull(message = "Description is required")
    @NotBlank(message = "Description cannot be blank")
    @Size(min = DESCRIPTION_MIN_LENGTH, max = DESCRIPTION_MAX_LENGTH,
            message = "Description must be between 5 and 500 characters")
    private String description;

    /**
     * Employee id submitting the claim.
     */
    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    /**
     * Expense/claim date (optional). If not provided, server will default to today's date.
     */
    @PastOrPresent(message = "Date cannot be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
}
