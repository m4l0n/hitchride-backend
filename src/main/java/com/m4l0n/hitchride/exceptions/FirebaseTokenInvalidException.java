package com.m4l0n.hitchride.exceptions;

// Programmer's Name: Ang Ru Xian
// Program Name: FirebaseTokenInvalidException.java
// Description: Exception for invalid Firebase token
// Last Modified: 22 July 2023

import org.springframework.security.authentication.BadCredentialsException;

public class FirebaseTokenInvalidException extends BadCredentialsException {
    public FirebaseTokenInvalidException(String msg) {
        super(msg);
    }
}
