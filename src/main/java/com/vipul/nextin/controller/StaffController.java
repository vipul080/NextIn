package com.vipul.nextin.controller;

import com.vipul.nextin.dto.CreateStaffRequest;
import com.vipul.nextin.dto.StaffResponse;
import com.vipul.nextin.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Everything under /api/admin requires the ADMIN role (see SecurityConfig).
@RestController
@RequestMapping("/api/admin")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("/staff")
    public ResponseEntity<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(staffService.createStaff(request));
    }

    @GetMapping("/clinics/{clinicId}/staff")
    public ResponseEntity<List<StaffResponse>> getStaffOfClinic(@PathVariable Long clinicId) {
        return ResponseEntity.ok(staffService.getStaffOfClinic(clinicId));
    }
}
