package com.vipul.nextin.exception;

public class ClinicNotFoundException extends RuntimeException {
    public ClinicNotFoundException(Long clinicId) {
        super("Clinic " + clinicId + " not found");
    }
}
