package com.vipul.nextin.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("An account with email " + email + " already exists");
    }
}
