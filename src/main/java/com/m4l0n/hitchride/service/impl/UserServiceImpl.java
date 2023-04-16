package com.m4l0n.hitchride.service.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final CollectionReference userRef;

    public UserServiceImpl(Firestore firestore) {
        this.userRef = firestore.collection("users");
    }


    @Override
    public User getProfile() {

        return new User(
                "1",
                "m4l0n",
                "test@mail.com",
                "wwefwef",
                "wefwefwef",
                "wefwef",
                12
        );
    }

}
