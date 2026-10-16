package com.vipul.nextin.exception;

public class DoctorNotFoundException extends ResourceNotFoundException {
    public DoctorNotFoundException(Long doctorId) {
        super("Doctor " + doctorId + " not found");
    }
}
