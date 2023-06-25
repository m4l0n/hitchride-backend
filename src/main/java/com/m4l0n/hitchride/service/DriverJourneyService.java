package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.pojos.DriverJourney;

import java.util.concurrent.ExecutionException;

public interface DriverJourneyService {

    DriverJourney createDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException;

    DriverJourney acceptDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException;

}
