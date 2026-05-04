package com.project.ReimbursementPortal.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    /**
     * The allowed email domain for user signups.
     */
    private final String allowedDomain;

    /**
     * @param domain from {@code app.allowed-email-domain} config
     */
    public EmailValidator(final @Value("${app.allowed-email-domain}") String domain) {
        this.allowedDomain = domain;
    }

    /**
     * @param email signup email
     * @throws RuntimeException missing config or suffix mismatch
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
