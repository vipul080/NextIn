package com.vipul.nextin.service;

import com.vipul.nextin.dto.LoginRequest;
import com.vipul.nextin.dto.LoginResponse;
import com.vipul.nextin.entity.User;
import com.vipul.nextin.repository.UserRepository;
import com.vipul.nextin.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Same error for "no such email" and "wrong password", so attackers cannot find out which emails exist.
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase(Locale.ROOT))
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        return new LoginResponse(jwtService.issue(user), "Bearer", jwtService.lifetimeSeconds(),
                user.getId(), user.getName(), user.getRole(), user.getClinicId());
    }
}
