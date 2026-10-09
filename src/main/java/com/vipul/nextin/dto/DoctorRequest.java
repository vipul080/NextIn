package com.vipul.nextin.dto;

import jakarta.validation.constraints.*;

public record DoctorRequest(
        @NotBlank(message = "Name is required") @Size(max = 150) String name,
        @NotBlank(message = "Speciality is required") @Size(max = 100) String speciality,
        @NotNull(message = "Average consult minutes is required")
        @Min(value = 1, message = "Average consult minutes must be at least 1")
        Integer avgConsultMinutes){
}
