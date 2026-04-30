package com.project.ReimbursementPortal.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    /**
     * Allowed email domain for user registration. Configured via application properties.
     */
    private final String allowedDomain;

    /**
     * Constructor that initializes the allowed email domain from application properties.
     * @param domain the allowed email domain (e.g., "@company.com")
     */
    public EmailValidator(final @Value("${app.allowed-email-domain}") String domain) {
        this.allowedDomain = domain;
    }

    /**
     * Validates that the provided email address ends with the allowed domain.
     * @param email the email address to validate
     */
    public void validate(final String email) {
        if (allowedDomain == null || allowedDomain.isEmpty()) {
            throw new RuntimeException("Allowed email domain not configured");
        }

        if (!email.endsWith(allowedDomain)) {
            throw new RuntimeException("Invalid email domain");
        }
    }
}
