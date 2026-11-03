package com.vipul.nextin.service;

import com.vipul.nextin.dto.CreateStaffRequest;
import com.vipul.nextin.dto.StaffResponse;
import com.vipul.nextin.entity.Clinic;
import com.vipul.nextin.entity.Role;
import com.vipul.nextin.entity.User;
import com.vipul.nextin.exception.BadRequestException;
import com.vipul.nextin.exception.ClinicNotFoundException;
import com.vipul.nextin.exception.EmailAlreadyUsedException;
import com.vipul.nextin.repository.ClinicRepository;
import com.vipul.nextin.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

// Admin-only: creates receptionist and doctor accounts for a clinic.
@Service
public class StaffService {

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffService(UserRepository userRepository, ClinicRepository clinicRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public StaffResponse createStaff(CreateStaffRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new BadRequestException("Admin accounts cannot be created through the API");
        }
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }
        Clinic clinic = clinicRepository.findById(request.clinicId())
                .orElseThrow(() -> new ClinicNotFoundException(request.clinicId()));

        User user = new User(request.name().trim(), email, passwordEncoder.encode(request.password()),
                request.role(), clinic);
        User saved = userRepository.save(user);
        return StaffResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffOfClinic(Long clinicId) {
        if (!clinicRepository.existsById(clinicId)) {
            throw new ClinicNotFoundException(clinicId);
        }
        return userRepository.findByClinic_IdOrderByNameAsc(clinicId).stream()
                .map(StaffResponse::from)
                .toList();
    }
}
