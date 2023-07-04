package com.m4l0n.hitchride.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.pojos.Ride;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface RideService {

    List<RideDTO> getRecentRides() throws ExecutionException, InterruptedException;

    RideDTO bookRide(RideDTO rideDTO) throws ExecutionException, InterruptedException, FirebaseMessagingException, TimeoutException;

    List<RideDTO> getRecentDrives() throws ExecutionException, InterruptedException;

    List<RideDTO> getUpcomingRides() throws ExecutionException, InterruptedException;

    Boolean cancelRide(String rideId) throws ExecutionException, InterruptedException, FirebaseMessagingException;

    Ride getRideById(String rideId) throws ExecutionException, InterruptedException;

    DocumentReference getRideReferenceById(String rideId) throws ExecutionException, InterruptedException;

    Boolean deleteRideByDriverJourney(String driverJourneyId) throws ExecutionException, InterruptedException, FirebaseMessagingException;

    RideDTO getRideByDriverJourney(String driverJourneyId) throws ExecutionException, InterruptedException;

    RideDTO completeRide(RideDTO rideDTO) throws ExecutionException, InterruptedException, FirebaseMessagingException;
}
