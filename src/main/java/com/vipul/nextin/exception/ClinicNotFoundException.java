package com.vipul.nextin.exception;

public class ClinicNotFoundException extends ResourceNotFoundException {
    public ClinicNotFoundException(Long clinicId) {
        super("Clinic " + clinicId + " not found");
    }
}
