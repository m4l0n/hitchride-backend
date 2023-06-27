package com.m4l0n.hitchride.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.dto.UserDTO;
import com.m4l0n.hitchride.pojos.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface UserService {

    UserDTO getProfile() throws ExecutionException, InterruptedException;

    UserDTO createUser(UserDTO user) throws ExecutionException, InterruptedException;

    UserDTO updateUser(UserDTO user) throws ExecutionException, InterruptedException, JsonProcessingException;

    User updateUserPoints(int points) throws ExecutionException, InterruptedException;

    String updateUserProfilePicture(MultipartFile imageFile) throws IOException, ExecutionException, InterruptedException;

    User loadUserByUsername(String username) throws ExecutionException, InterruptedException;

    Map<String, GeoPoint> getUserSavedLocations() throws ExecutionException, InterruptedException;

    Map<String, GeoPoint> saveUserLocation(Map<String, GeoPoint> location) throws ExecutionException, InterruptedException;

    Map<String, GeoPoint> deleteUserLocation(Map<String, GeoPoint> location) throws ExecutionException, InterruptedException;

    Map<String, GeoPoint> updateUserLocation(Map<String, GeoPoint> location) throws ExecutionException, InterruptedException;

}
