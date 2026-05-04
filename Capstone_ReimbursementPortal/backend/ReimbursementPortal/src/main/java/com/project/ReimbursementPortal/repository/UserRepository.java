package com.project.ReimbursementPortal.repository;

import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * @param email unique login
     * @return Optional containing the User with the specified email
     */
    Optional<User> findByEmail(String email);

    /**
     * @param role ADMIN/MANAGER/EMPLOYEE bucket
     * @return all users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * @param managerId parent user PK for org chart lookups
     * @return all users with the specified managerId
     */
    List<User> findByManagerId(Long managerId);
}
