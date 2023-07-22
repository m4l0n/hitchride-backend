package com.m4l0n.hitchride.service.impl;

// Programmer's Name: Ang Ru Xian
// Program Name: FirebaseServiceImpl.java
// Description: Implementation of FirebaseService interface, that parses the JWT token
// Last Modified: 22 July 2023

import com.google.firebase.auth.FirebaseToken;
import com.m4l0n.hitchride.service.FirebaseService;
import com.m4l0n.hitchride.service.shared.FirebaseTokenParser;
import org.springframework.stereotype.Service;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public FirebaseToken parseToken(String token) {
        return new FirebaseTokenParser().parseToken(token);
    }

}
