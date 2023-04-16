package com.m4l0n.hitchride.service.impl;

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
