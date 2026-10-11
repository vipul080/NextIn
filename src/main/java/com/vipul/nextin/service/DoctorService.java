package com.vipul.nextin.service;


import com.vipul.nextin.dto.DoctorRequest;
import com.vipul.nextin.dto.DoctorResponse;
import com.vipul.nextin.entity.Clinic;
import com.vipul.nextin.entity.Doctor;
import com.vipul.nextin.exception.ClinicNotFoundException;
import com.vipul.nextin.repository.ClinicRepository;
import com.vipul.nextin.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;

    public DoctorService(DoctorRepository doctorRepository, ClinicRepository clinicRepository) {
        this.doctorRepository = doctorRepository;
        this.clinicRepository = clinicRepository;
    }

    @Transactional
    public DoctorResponse createDoctor(Long clinicId, DoctorRequest request) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ClinicNotFoundException(clinicId));
        Doctor doctor = new Doctor(clinic,
                request.name(),
                request.speciality(),
                request.avgConsultMinutes());
        Doctor saved = doctorRepository.save(doctor);
        return DoctorResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctorsByClinic(Long clinicId) {
        if (!clinicRepository.existsById(clinicId)) {
            throw new ClinicNotFoundException(clinicId);
        }
        return doctorRepository.findByClinicId(clinicId).stream()
                .map(DoctorResponse::from)
                .toList();
    }
}
