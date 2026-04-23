package com.project.ReimbursementPortal.validation;

import com.project.ReimbursementPortal.config.AppProperties;
import org.springframework.stereotype.Component;


@Component
public class EmailValidation {
    /**
     * Application-level configuration properties.
     */
    private final AppProperties appProperties;

    /**
     * Constructor for EmailValidation that initializes the AppProperties dependency.
     * @param appProperties the application properties containing the allowed email domain configuration
     */
    public EmailValidation(final AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Validates that the provided email belongs to the configured company domain.
     *
     * @param email email to validate
     * @throws IllegalArgumentException if the email does not end with the allowed domain
     */
    public void validateCompanyEmail(final String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        String allowedDomain = appProperties.getAllowedEmailDomain() == null
                ? ""
                : appProperties.getAllowedEmailDomain().trim().toLowerCase();

        if (!normalizedEmail.endsWith(allowedDomain)) {
            throw new IllegalArgumentException("Email must end with " + allowedDomain);
        }
    }

    /**
     * Backward-compatible alias for validating company email addresses.
     *
     * @param email email to validate
     */
    public void validateEmail(final String email) {
        validateCompanyEmail(email);
    }
}
