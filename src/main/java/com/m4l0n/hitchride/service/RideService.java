package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.dto.RideDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface RideService {

    List<RideDTO> getRecentRides() throws ExecutionException, InterruptedException;

    RideDTO acceptRide(RideDTO rideDTO) throws ExecutionException, InterruptedException;

    List<RideDTO> getRecentDrives() throws ExecutionException, InterruptedException;

    List<RideDTO> getUpcomingRides() throws ExecutionException, InterruptedException;
}
