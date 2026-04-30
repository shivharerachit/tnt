package com.project.ReimbursementPortal.enums;

public enum UserRole {
    /**
     * Role with full access to all system features, including user management and claim oversight.
     */
    ADMIN,

    /**
     * Role with permissions to review and manage claims submitted by employees, but without access to user management features.
     */
    MANAGER,

    /**
     * Role with permissions to submit reimbursement claims and view their own claim history,
     * but without access to user management or claim review features.
     */
    EMPLOYEE
}
