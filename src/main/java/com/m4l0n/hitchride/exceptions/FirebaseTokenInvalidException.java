package com.m4l0n.hitchride.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class FirebaseTokenInvalidException extends BadCredentialsException {
    public FirebaseTokenInvalidException(String msg) {
        super(msg);
    }
}
