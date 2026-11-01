package com.vipul.nextin.service;

import com.vipul.nextin.dto.ClinicRequest;
import com.vipul.nextin.dto.ClinicResponse;
import com.vipul.nextin.entity.Clinic;
import com.vipul.nextin.exception.ClinicNotFoundException;
import com.vipul.nextin.repository.ClinicRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    @Transactional(readOnly = true)
    public List<ClinicResponse> getAllClinics(){
        return clinicRepository.findAll().stream().map(ClinicResponse::from).toList();
    }

    @Transactional
    public ClinicResponse createClinic(ClinicRequest request){
        Clinic clinic = new Clinic(request.name(), request.city(), request.address() , request.phone());

        Clinic saved = clinicRepository.save(clinic);

        return ClinicResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ClinicResponse getClinic(Long clinicId){
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(()-> new ClinicNotFoundException(clinicId));

        return ClinicResponse.from(clinic);
    }
}
