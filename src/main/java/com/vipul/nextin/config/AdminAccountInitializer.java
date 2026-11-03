package com.vipul.nextin.config;

import com.vipul.nextin.entity.Role;
import com.vipul.nextin.entity.User;
import com.vipul.nextin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

// Solves the chicken-and-egg problem: only an admin can create accounts, so who creates the first admin?
// On startup, if no admin exists and ADMIN_EMAIL / ADMIN_PASSWORD are set, create one.
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminAccountInitializer(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   @Value("${app.admin.email:}") String adminEmail,
                                   @Value("${app.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.warn("No admin account exists. Set ADMIN_EMAIL and ADMIN_PASSWORD to create one on startup.");
            return;
        }
        String email = adminEmail.trim().toLowerCase(Locale.ROOT);
        userRepository.save(new User("Admin", email, passwordEncoder.encode(adminPassword), Role.ADMIN, null));
        log.info("Created admin account {}", email);
    }
}
