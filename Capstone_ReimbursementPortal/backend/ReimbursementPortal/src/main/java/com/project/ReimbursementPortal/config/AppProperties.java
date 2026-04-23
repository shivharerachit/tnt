package com.project.ReimbursementPortal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    /**
     * The allowed email domain for user registration, configured in application properties.
     */
    private String allowedEmailDomain;
}
