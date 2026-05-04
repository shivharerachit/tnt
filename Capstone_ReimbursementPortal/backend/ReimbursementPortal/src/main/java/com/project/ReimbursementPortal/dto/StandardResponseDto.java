package com.project.ReimbursementPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StandardResponseDto<T> {

    /**
     * Indicates whether the API request was successful or not.
     * This field is used by clients to quickly determine if the operation succeeded without needing to parse the message or data.
     */
    private boolean success;

    /**
     * A human-readable message providing additional information about the API response.
     */
    private String message;

    /**
     * The actual data returned from the API.
     * This is a generic type, allowing it to hold any type of response data (e.g., claim details, authentication info, etc.).
     */
    private T data;
}
