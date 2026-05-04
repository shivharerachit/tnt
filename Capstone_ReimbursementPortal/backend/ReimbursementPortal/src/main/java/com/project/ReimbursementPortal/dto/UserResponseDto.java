package com.project.ReimbursementPortal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

    /**
     * User identifier.
     * This is a unique value that can be used to reference the user in other API calls and for authorization purposes.
     */
    private Long id;

    /**
     * Name of the user.
     * This is included in the response for convenience,
     * allowing the client to display the user's name without needing an additional API call.
     */
    private String name;

    /**
     * Email of the user.
     * This is included in the response for convenience,
     * allowing the client to display the user's email without needing an additional API call.
     * It can also be used for client-side logic that may require the user's email.
     */
    private String email;

    /**
     * Role of the user (e.g., ADMIN, EMPLOYEE).
     */
    private String role;

    /**
     * Manager ID of the user. This field is relevant for employees, indicating who their manager is.
     */
    private Long managerId;
}
