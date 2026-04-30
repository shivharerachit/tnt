package com.project.ReimbursementPortal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for user login authentication.
 * Contains validation for email and password fields.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {

    /**
     * Minimum and maximum size constraints for the password field.
     */
    private static final int MIN_SIZE = 6;

    /**
     * Maximum size constraint for the password field. Set to 100 to allow for hashed passwords and future-proofing.
     */
    private static final int MAX_SIZE = 100;
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
}
