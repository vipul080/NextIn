package com.vipul.nextin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookTokenRequest(
        @NotBlank(message = "Patient name is required")
        @Size(max = 250, message = "Patient name must be at most 250 characters")
        String patientName,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^\\+?[0-9]{10,14}$",
                message = "Phone number must be 10-14 digits, optionally starting with +")
        String patientPhone
) {}
