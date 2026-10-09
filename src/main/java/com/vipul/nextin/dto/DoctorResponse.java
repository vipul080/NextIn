package com.vipul.nextin.dto;

import com.vipul.nextin.entity.Clinic;
import com.vipul.nextin.entity.Doctor;

import java.time.Instant;

public record DoctorResponse(
        Long id,
        Long clinicId,
        String clinicName,
        String name,
        String speciality,
        int avgConsultMinutes,
        Instant createdAt) {

    public static DoctorResponse from(Doctor doctor){
        return new DoctorResponse(doctor.getId(),
                                    doctor.getClinic().getId(),
                                    doctor.getClinic().getName(),
                                    doctor.getName(),
                                    doctor.getSpeciality(),
                                    doctor.getAvgConsultMinutes(),
                                    doctor.getCreatedAt());
    }
}
