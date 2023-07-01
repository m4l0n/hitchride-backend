package com.m4l0n.hitchride.service;

import com.google.cloud.firestore.DocumentReference;
import com.m4l0n.hitchride.dto.RideDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface RideService {

    List<RideDTO> getRecentRides() throws ExecutionException, InterruptedException;

    RideDTO acceptRide(RideDTO rideDTO) throws ExecutionException, InterruptedException;

    List<RideDTO> getRecentDrives() throws ExecutionException, InterruptedException;

    List<RideDTO> getUpcomingRides() throws ExecutionException, InterruptedException;

    Boolean cancelRide(RideDTO rideDTO) throws ExecutionException, InterruptedException;

    RideDTO getRideById(String rideId) throws ExecutionException, InterruptedException;

    DocumentReference getRideReferenceById(String rideId) throws ExecutionException, InterruptedException;
}
