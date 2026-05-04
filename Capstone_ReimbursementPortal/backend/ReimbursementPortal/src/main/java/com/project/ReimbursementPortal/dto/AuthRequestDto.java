package com.project.ReimbursementPortal.dto;

import jakarta.validation.constraints.NotBlank;
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
     * User's email address.
     */
    @NotBlank(message = "Email cannot be blank")
    private String email;

    /**
     * User's password.
     */
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
