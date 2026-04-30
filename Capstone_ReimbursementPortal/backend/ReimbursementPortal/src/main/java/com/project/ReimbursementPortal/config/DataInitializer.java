package com.project.ReimbursementPortal.config;

import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    /**
     * User repository for database operations.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder for hashing default admin password.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a data initializer.
     * @param userRepository user repository
     * @param passwordEncoder password encoder
     */
    public DataInitializer(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Initializes default data on application startup.
     * @param args incoming main method arguments
     */
    @Override
    public void run(final String... args) {

        // Create default admin if DB empty
        if (userRepository.count() == 0) {

            User admin =  new User();
            admin.setName("Admin");
            admin.setEmail("admin@company.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);

            userRepository.save(admin);

            System.out.println("Default admin created!");
        }
    }
}
