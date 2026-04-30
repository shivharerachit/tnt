package com.project.ReimbursementPortal.repository;

import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * @param email user email
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds users by their role (e.g., EMPLOYEE, MANAGER, ADMIN).
     * @param role user role
     * @return list of users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * Finds users who report to a specific manager.
     * @param managerId manager id
     * @return list of users who report to the specified manager
     */
    List<User> findByManagerId(Long managerId);
}
