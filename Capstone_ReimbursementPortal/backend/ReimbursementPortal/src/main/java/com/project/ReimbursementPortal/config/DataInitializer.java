package com.project.ReimbursementPortal.config;

import com.project.ReimbursementPortal.enums.UserRole;
import com.project.ReimbursementPortal.entity.User;
import com.project.ReimbursementPortal.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    /**
     * for user data access.
     */
    private final UserRepository userRepository;

    /**
     * for hashing the default admin password.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * for seeding the default admin user.
     */
    @Value("${app.default-admin.name}")
    private String defaultAdminName;

    /**
     * for seeding the default admin user.
     */
    @Value("${app.default-admin.email}")
    private String defaultAdminEmail;

    /**
     * for seeding the default admin user.
     */
    @Value("${app.default-admin.password}")
    private String defaultAdminPassword;

    /**
     * @param userRepository repo
     * @param passwordEncoder bcrypt bean
     */
    public DataInitializer(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * First boot with empty DB → seed one ADMIN so login works out of the box.
     *
     * @param args unused
     */
    @Override
    public void run(final String... args) {

        if (userRepository.count() == 0) {

            User admin = new User();
            admin.setName(defaultAdminName);
            admin.setEmail(defaultAdminEmail);
            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            admin.setRole(UserRole.ADMIN);

            userRepository.save(admin);

            System.out.println("Default admin created!");
        }
    }
}
