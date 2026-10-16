package com.vipul.nextin.exception;

public class TokenNotFoundException extends ResourceNotFoundException {
    public TokenNotFoundException(Long tokenId) {
        super("Token " + tokenId + " not found");
    }
}
