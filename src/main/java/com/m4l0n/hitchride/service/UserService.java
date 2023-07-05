package com.m4l0n.hitchride.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.firestore.DocumentReference;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface UserService {

    HitchRideUser getProfile() throws ExecutionException, InterruptedException;

    HitchRideUser createUser(HitchRideUser user) throws ExecutionException, InterruptedException;

    HitchRideUser updateUser(HitchRideUser user) throws ExecutionException, InterruptedException, JsonProcessingException;

    HitchRideUser updateUserPoints(String userId, int points) throws ExecutionException, InterruptedException;

    String updateUserProfilePicture(MultipartFile imageFile) throws IOException, ExecutionException, InterruptedException;

    HitchRideUser loadUserByUsername(String username) throws ExecutionException, InterruptedException;

    HitchRideUser updateDriverInfo(HitchRideUser user) throws ExecutionException, InterruptedException;

    Boolean updateDriverRatings(Integer newRating) throws ExecutionException, InterruptedException;

    DocumentReference getUserDocumentReference(String userId) throws ExecutionException, InterruptedException;
}
