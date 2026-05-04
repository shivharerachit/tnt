package com.project.ReimbursementPortal.repository;

import com.project.ReimbursementPortal.entity.Claim;
import com.project.ReimbursementPortal.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    /**
     * @param employeeId submitter FK
     * @return all claims by this employee, regardless of status or reviewer
     */
    List<Claim> findByEmployeeId(Long employeeId);

    /**
     * @param employeeId submitter
     * @param pageable spring page/size/sort
     * @return employee slice, regardless of status or reviewer
     */
    Page<Claim> findByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * @param reviewerId queue owner
     * @return all claims assigned to this reviewer
     */
    List<Claim> findByReviewerId(Long reviewerId);

    /**
     * @param reviewerId reviewer
     * @param pageable spring page/size/sort
     * @return reviewer slice, regardless of status or submitter
     */
    Page<Claim> findByReviewerId(Long reviewerId, Pageable pageable);


    /**
     * @param employeeId submitter FK
     * @param status filter (non-null branch in service)
     * @param pageable spring page/size/sort
     * @return employee slice
     */
    Page<Claim> findByEmployeeIdAndStatus(Long employeeId, ClaimStatus status, Pageable pageable);

    /**
     * @param reviewerId queue owner FK
     * @param status optional narrowing in service calls
     * @param pageable paging
     * @return reviewer slice
     */
    Page<Claim> findByReviewerIdAndStatus(Long reviewerId, ClaimStatus status, Pageable pageable);

    /**
     * @param status enum column match
     * @param pageable paging
     * @return filtered global page for admins
     */
    Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);
}
