package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.SearchRideCriteria;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DriverJourneyService {

    DriverJourneyDTO createDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException;

    DriverJourney acceptDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException;

    CompletableFuture<List<DriverJourneyDTO>> searchRidesFromDriverJourneys(SearchRideCriteria searchRideCriteria) throws Exception;

    DriverJourneyDTO deleteDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException;

    List<DriverJourneyDTO> getUserDriverJourneys() throws ExecutionException, InterruptedException;
}
