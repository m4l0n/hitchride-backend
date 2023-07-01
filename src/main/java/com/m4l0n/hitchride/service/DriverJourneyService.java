package com.m4l0n.hitchride.service;

import com.google.cloud.firestore.DocumentReference;
import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.dto.SearchRideCriteriaDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DriverJourneyService {

    DriverJourneyDTO createDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException;

    boolean acceptDriverJourney(String driverJourney) throws ExecutionException, InterruptedException;

    CompletableFuture<List<DriverJourneyDTO>> searchRidesFromDriverJourneys(SearchRideCriteriaDTO searchRideCriteria) throws Exception;

    DriverJourneyDTO deleteDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException;

    List<DriverJourneyDTO> getUserDriverJourneys() throws ExecutionException, InterruptedException;

    DriverJourneyDTO getDriverJourneyById(String id) throws ExecutionException, InterruptedException;

    DocumentReference getDriverJourneyRefById(String id);

    List<DocumentReference> getDriverJourneyRefsByDriverUserId(String userId) throws ExecutionException, InterruptedException;

    List<DocumentReference> getFutureDriverJourneyRefs() throws ExecutionException, InterruptedException;
}
