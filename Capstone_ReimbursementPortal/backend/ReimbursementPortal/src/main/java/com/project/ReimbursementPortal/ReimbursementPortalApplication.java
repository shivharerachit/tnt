package com.project.ReimbursementPortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class ReimbursementPortalApplication {
    /**
     * Main method that serves as the entry point for the Spring Boot application.
     * @param args command-line arguments (not used in this application)
     */
    public static void main(final String[] args) {
        SpringApplication.run(ReimbursementPortalApplication.class, args);
    }

}
