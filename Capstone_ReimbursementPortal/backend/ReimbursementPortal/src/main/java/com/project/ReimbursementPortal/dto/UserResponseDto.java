package com.project.ReimbursementPortal.dto;

import com.project.ReimbursementPortal.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
public class UserResponseDto {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The email of the user.
     * This should be a valid email address and is used for communication and login purposes.
     */
    private String email;

    /**
     * The role of the user in the system.
     * This determines the user's permissions and access levels. Possible values include ADMIN, MANAGER, and EMPLOYEE.
     */
    private UserRole role;

    /**
     * The ID of the user's manager.
     * This field is optional and can be null if the user does not have a manager.
     * It is used to establish a hierarchical relationship between users in the organization.
     */
    private Long managerId;

    /**
     * The list of IDs of the user's reportees.
     * This field is optional and can be null if the user does not have any reportees.
     * It is used to represent the direct subordinates of a manager in the organizational structure.
     */
    private List<Long> reporteesIds;

    /**
     * The timestamp when the user was created in the system.
     */
    private LocalDateTime createdAt;
}
