package com.vipul.nextin.controller;

import com.vipul.nextin.dto.ClinicRequest;
import com.vipul.nextin.dto.ClinicResponse;
import com.vipul.nextin.service.ClinicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping
    public ResponseEntity<List<ClinicResponse>> findAllClinics() {

        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    @PostMapping
    public ResponseEntity<ClinicResponse> createClinic(@Valid @RequestBody ClinicRequest request){
        ClinicResponse response = clinicService.createClinic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
