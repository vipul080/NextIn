package com.vipul.nextin.dto;

import jakarta.validation.constraints.NotBlank;

// Patients have no login, so cancelling requires the phone number the token was booked with.
public record CancelTokenRequest(
        @NotBlank(message = "Phone is required") String patientPhone
) {}
