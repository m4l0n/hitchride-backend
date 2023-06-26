package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.SearchRideCriteria;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DriverJourneyService {

    DriverJourney createDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException;

    DriverJourney acceptDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException;

    CompletableFuture<List<DriverJourney>> searchRidesFromDriverJourneys(SearchRideCriteria searchRideCriteria) throws Exception;

    DriverJourney deleteDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException;

    List<DriverJourney> getUserDriverJourneys() throws ExecutionException, InterruptedException;
}
