package com.m4l0n.hitchride.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.pojos.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface UserService {

    User getProfile() throws ExecutionException, InterruptedException;

    User createUser(User user) throws ExecutionException, InterruptedException;

    User updateUser(User user) throws ExecutionException, InterruptedException, JsonProcessingException;
    User loadUserByUsername(String username) throws ExecutionException, InterruptedException;
}
