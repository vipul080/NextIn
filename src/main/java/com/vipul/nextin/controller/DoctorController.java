package com.vipul.nextin.controller;

import com.vipul.nextin.dto.DoctorRequest;
import com.vipul.nextin.dto.DoctorResponse;
import com.vipul.nextin.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics/{clinicId}/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@PathVariable Long clinicId,
                                                       @Valid @RequestBody DoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(clinicId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getDoctorsByClinic(@PathVariable Long clinicId) {
        return ResponseEntity.ok(doctorService.getDoctorsByClinic(clinicId));
    }
}
