package com.project.ReimbursementPortal.enums;

public enum ClaimStatus {
    /**
     * Status indicating that the claim has been submitted by the employee and is awaiting review.
     */
    SUBMITTED,

    /**
     * Status indicating that the claim is currently being reviewed by a manager or administrator.
     */
    APPROVED,

    /**
     * Status indicating that the claim has been reviewed and rejected by a manager or administrator.
     */
    REJECTED
}
