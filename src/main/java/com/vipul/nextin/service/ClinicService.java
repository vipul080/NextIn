package com.vipul.nextin.service;

import com.vipul.nextin.entity.Clinic;
import com.vipul.nextin.repository.ClinicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public List<Clinic> getAllClinics(){

        return clinicRepository.findAll();
    }
}
