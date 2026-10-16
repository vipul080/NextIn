package com.vipul.nextin.exception;

// A status change the state machine does not allow, e.g. completing a token that was never called in.
public class InvalidTokenTransitionException extends RuntimeException {
    public InvalidTokenTransitionException(String message) {
        super(message);
    }
}
