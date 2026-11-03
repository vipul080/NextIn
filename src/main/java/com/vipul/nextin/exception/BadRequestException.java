package com.vipul.nextin.exception;

// A request that passes field validation but breaks a business rule, e.g. creating an ADMIN through the API.
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
