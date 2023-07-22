package com.m4l0n.hitchride.service.shared;

// Programmer's Name: Ang Ru Xian
// Program Name: FirebaseTokenParser.java
// Description: Service that parses the JWT token
// Last Modified: 22 July 2023

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.m4l0n.hitchride.exceptions.FirebaseTokenInvalidException;
import org.springframework.util.StringUtils;

public class FirebaseTokenParser {

    public FirebaseToken parseToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Token is empty");
        }
        try {
            return FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            throw new FirebaseTokenInvalidException(e.getMessage());
        }
    }

}
