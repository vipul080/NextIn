package com.vipul.nextin.exception;

// Parent of every "X not found" error, so one handler can turn them all into 404.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
