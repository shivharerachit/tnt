package com.project.ReimbursementPortal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating or updating a user.
 * Contains validation for all mandatory fields.
 */
@Getter
@Setter
public class UserRequestDto {

    /**
     * Minimum and maximum size constraints for the name field.
     */
    private static final int NAME_MIN_SIZE = 2;

    /**
     * Maximum size constraint for the name field. Set to 100 to allow for full names and future-proofing.
     */
    private static final int NAME_MAX_SIZE = 100;

    /**
     * Minimum and maximum size constraints for the password field.
     */
    private static final int MIN_SIZE = 6;

    /**
     * Maximum size constraint for the password field. Set to 100 to allow for hashed passwords and future-proofing.
     */
    private static final int MAX_SIZE = 100;

    /**
     * Minimum and maximum size constraints for the name field.
     */
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = NAME_MIN_SIZE, max = NAME_MAX_SIZE, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * User's email address. Must be a valid email format and cannot be null or blank.
     */
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * User's password. Must be between 6 and 100 characters, and cannot be null or blank.
     */
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = MIN_SIZE, max = MAX_SIZE, message = "Password must be between 6 and 100 characters")
    private String password;

    /**
     * User's role (e.g., ADMIN, EMPLOYEE). Cannot be null or blank.
     * Validation for allowed roles will be handled in the service layer.
     */
    @NotNull(message = "Role is required")
    @NotBlank(message = "Role cannot be blank")
    private String role;

    /**
     * Manager ID for EMPLOYEE role. Optional for ADMIN role. If provided, must correspond to an existing user with ADMIN role.
     */
    private Long managerId;
}
