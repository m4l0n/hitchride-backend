package com.m4l0n.hitchride.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class HitchrideException extends BadCredentialsException {
    public HitchrideException(String msg) {
        super(msg);
    }
}
