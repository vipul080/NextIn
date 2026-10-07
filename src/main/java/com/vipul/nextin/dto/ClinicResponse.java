package com.vipul.nextin.dto;

import com.vipul.nextin.entity.Clinic;

import java.time.Instant;


public record ClinicResponse(
        Long id, String name, String city, String address, String phone, Instant createdAt
) {
    public static ClinicResponse from(Clinic clinic) {
        return new ClinicResponse(clinic.getId(), clinic.getName(), clinic.getCity(),  clinic.getAddress(), clinic.getPhone(), clinic.getCreatedAt());
    }
}
