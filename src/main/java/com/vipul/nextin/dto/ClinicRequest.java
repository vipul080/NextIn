package com.vipul.nextin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClinicRequest(
        @NotBlank(message = "Name is required") @Size(max = 150) String name,
        @NotBlank(message = "City is required") @Size(max = 100) String city,
        @NotBlank(message = "Address is required") @Size(max = 255) String address,
        @NotBlank(message = "Phone is required") @Pattern(regexp = "^\\+?[0-9]{10,14}$", message = "Phone number must be between 10-14 digits optionally starting with +") String phone
) {}