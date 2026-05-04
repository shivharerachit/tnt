package com.project.ReimbursementPortal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

    /**
     * The current password of the user.
     * This is required to verify the user's identity before changing the password.
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /**
     * The new password for the user.
     * This is the password the user wants to change to.
     */
    @NotBlank(message = "New password is required")
    private String newPassword;
}

