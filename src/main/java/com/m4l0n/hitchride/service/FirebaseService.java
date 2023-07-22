package com.m4l0n.hitchride.service;

// Programmer's Name: Ang Ru Xian
// Program Name: FirebaseService.java
// Description: An interface that consists of methods to parse JWT token from client
// Last Modified: 22 July 2023

import com.google.firebase.auth.FirebaseToken;

public interface FirebaseService {

    FirebaseToken parseToken(String token);

}
