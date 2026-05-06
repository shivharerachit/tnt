package com.project.ReimbursementPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    /**
     * User ID of the authenticated user. This is a unique identifier that can be used for authorization and other operations.
     */
    private Long userId;

    /**
     * Name of the authenticated user.
     * This is included in the response for convenience,
     * allowing the client to display the user's name without needing an additional API call.
     */
    private String name;

    /**
     * Email of the authenticated user.
     * This is included in the response for convenience,
     * allowing the client to display the user's email without needing an additional API call.
     * It can also be used for client-side logic that may require the user's email.
     */
    private String email;

    /**
     * Role of the authenticated user (e.g., ADMIN, EMPLOYEE).
     */
    private String role;
}
