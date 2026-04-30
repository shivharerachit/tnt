package com.project.ReimbursementPortal.repository;

import com.project.ReimbursementPortal.entity.Claim;
import com.project.ReimbursementPortal.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    /**
     * Finds claims submitted by a specific employee.
     * @param employeeId employee id
     * @return list of claims
     */
    List<Claim> findByEmployeeId(Long employeeId);

    /**
     * Finds claims submitted by a specific employee with pagination.
     * @param employeeId employee id
     * @param pageable pagination information
     * @return page of claims
     */
    Page<Claim> findByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * Finds claims assigned to a specific reviewer.
     * @param reviewerId reviewer id
     * @return list of claims
     */
    List<Claim> findByReviewerId(Long reviewerId);

    /**
     * Finds claims assigned to a specific reviewer with pagination.
     * @param reviewerId reviewer id
     * @param pageable pagination information
     * @return page of claims
     */
    Page<Claim> findByReviewerId(Long reviewerId, Pageable pageable);

    /**
     * Finds claims by their status (e.g., SUBMITTED, APPROVED, REJECTED).
     * @param status claim status
     * @return list of claims
     */
    List<Claim> findByStatus(ClaimStatus status);

    /**
     * Finds claims assigned to a specific reviewer with a specific status.
     * @param reviewerId reviewer id
     * @param status claim status
     * @return list of claims
     */
    List<Claim> findByReviewerIdAndStatus(Long reviewerId, ClaimStatus status);
}
