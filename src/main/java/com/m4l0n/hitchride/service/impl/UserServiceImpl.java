package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final CollectionReference userRef;
    private final AuthenticationService authenticationService;

    public UserServiceImpl(Firestore firestore, AuthenticationService authenticationService) {
        this.userRef = firestore.collection("users");
        this.authenticationService = authenticationService;
    }


    @Override
    public User getProfile() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        User user = loadUserByUsername(currentLoggedInUser);

        log.info("getProfile: {}", user != null ? user.getId() : null);

        return user;
    }

    @Override
    public User createUser(User user) throws ExecutionException, InterruptedException {
        //If user does not have an ID, set the ID to the Firebase Authentication ID
        if (user.getId() == null) {
            user.setId(authenticationService.getAuthenticatedUsername());
        }
        //Checks first if the user exists
        User findUser = loadUserByUsername(user.getId());
        if (findUser == null) {
            ApiFuture<WriteResult> result = userRef.document(user.getId()).set(user);
            //Wait for the result to finish
            result.get();
            return user;
        }
        return null;
    }

    @Override
    public User loadUserByUsername(String username) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> documentSnapshot = userRef.document(username).get();
        DocumentSnapshot document = documentSnapshot.get();

        User user;

        if (document.exists()) {
            user = document.toObject(User.class);
        } else {
            user = null;
        }

        log.info("loadUserByUsername: {}", user != null ? user.getId() : null);

        return user;
    }

}
