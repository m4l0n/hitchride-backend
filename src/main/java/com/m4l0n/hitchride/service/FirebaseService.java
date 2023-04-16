package com.m4l0n.hitchride.service;

import com.google.firebase.auth.FirebaseToken;

public interface FirebaseService {

    FirebaseToken parseToken(String token);

}
