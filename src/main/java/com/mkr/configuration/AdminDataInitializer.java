package com.mkr.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mkr.enums.Role;
import com.mkr.model.User;
import com.mkr.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@jobportal.com}")
    private String adminEmail;

    @Value("${app.admin.name:Job Portal Admin}")
    private String adminName;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        // Check whether an Admin account already exists
        if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
            System.out.println("Admin account already exists.");
            return;
        }

        // Do not create an account without a configured password
        if (adminPassword == null || adminPassword.isBlank()) {
            System.out.println("Admin not created. Configure ADMIN_PASSWORD.");
            return;
        }

        // Create Admin user
        User admin = new User();

        admin.setFullName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setActive(true);

        userRepository.save(admin);

        System.out.println("Admin account created successfully.");
    }
}