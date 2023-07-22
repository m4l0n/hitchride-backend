package com.m4l0n.hitchride.exceptions;

// Programmer's Name: Ang Ru Xian
// Program Name: HitchrideException.java
// Description: Exception for all exceptions that are thrown in the program
// Last Modified: 22 July 2023

import org.springframework.security.authentication.BadCredentialsException;

public class HitchrideException extends BadCredentialsException {
    public HitchrideException(String msg) {
        super(msg);
    }
}
