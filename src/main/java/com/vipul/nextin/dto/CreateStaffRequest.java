package com.vipul.nextin.dto;

import com.vipul.nextin.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStaffRequest(
        @NotBlank(message = "Name is required") @Size(max = 150) String name,
        @NotBlank(message = "Email is required") @Email(message = "Email is not valid") @Size(max = 255) String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be 8-72 characters")
        String password,
        @NotNull(message = "Role is required (RECEPTIONIST or DOCTOR)") Role role,
        @NotNull(message = "Clinic id is required") Long clinicId
) {}
