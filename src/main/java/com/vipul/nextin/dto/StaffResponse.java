package com.vipul.nextin.dto;

import com.vipul.nextin.entity.Role;
import com.vipul.nextin.entity.User;

import java.time.Instant;

// Never includes the password hash.
public record StaffResponse(Long id, String name, String email, Role role, Long clinicId, Instant createdAt) {

    public static StaffResponse from(User user) {
        return new StaffResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(),
                user.getClinicId(), user.getCreatedAt());
    }
}
