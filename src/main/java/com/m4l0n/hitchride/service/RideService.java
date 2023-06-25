package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.pojos.Ride;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface RideService {

    List<Ride> getRecentRides() throws ExecutionException, InterruptedException;

    Ride acceptRide(Ride ride) throws ExecutionException, InterruptedException;

    List<Ride> getRideHistory() throws ExecutionException, InterruptedException;

    List<Ride> getDriveHistory() throws ExecutionException, InterruptedException;

}
