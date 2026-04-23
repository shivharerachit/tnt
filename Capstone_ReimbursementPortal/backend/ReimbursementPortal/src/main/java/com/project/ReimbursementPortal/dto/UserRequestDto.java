package com.project.ReimbursementPortal.dto;

import com.project.ReimbursementPortal.entity.UserRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDto {

    /**
     * The name of the user. This field is required and must not be blank.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * The email of the user. This field is required, must not be blank, and must be a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The password of the user. This field is required and must not be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * The role of the user. This field is required and must be one of the following values: ADMIN, MANAGER, EMPLOYEE.
     */
    @NotNull(message = "Role is required and must be one of: ADMIN, MANAGER, EMPLOYEE")
    private UserRole role;

    /**
     * The ID of the user's manager. This field is optional and can be null if the user does not have a manager.
     */
    @Nullable
    private Long managerId;

    /**
     * The list of IDs of the user's reportees. This field is optional and can be null if the user does not have any reportees.
     */
    @Nullable
    private List<Long> reporteesIds;
}
