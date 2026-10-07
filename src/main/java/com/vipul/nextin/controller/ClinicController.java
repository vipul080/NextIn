package com.vipul.nextin.controller;


import com.vipul.nextin.entity.Clinic;
import com.vipul.nextin.entity.Doctor;
import com.vipul.nextin.service.ClinicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping
    public ResponseEntity<List<Clinic>> findAllClinics() {

        return ResponseEntity.ok(clinicService.getAllClinics());
    }
}
