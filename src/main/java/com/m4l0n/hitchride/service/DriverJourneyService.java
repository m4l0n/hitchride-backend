package com.m4l0n.hitchride.service;

// Programmer's Name: Ang Ru Xian
// Program Name: DriverJourneyService.java
// Description: An interface that consists of methods to be implemented by the DriverJourneyService class
// Last Modified: 22 July 2023

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.dto.SearchRideCriteriaDTO;
import com.m4l0n.hitchride.pojos.DriverJourney;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DriverJourneyService {

    DriverJourneyDTO createDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException;

    void acceptDriverJourney(String driverJourney, Transaction transaction);

    CompletableFuture<List<DriverJourneyDTO>> searchRidesFromDriverJourneys(SearchRideCriteriaDTO searchRideCriteria) throws Exception;

    void deleteDriverJourney(String djId) throws ExecutionException, InterruptedException, FirebaseMessagingException;

    List<DriverJourneyDTO> getUserDriverJourneys() throws ExecutionException, InterruptedException;

    DriverJourney getDriverJourneyById(String id) throws ExecutionException, InterruptedException;

    DocumentReference getDriverJourneyRefById(String id);

    List<DocumentReference> getDriverJourneyRefsByDriverUserId(String userId) throws ExecutionException, InterruptedException;

    List<DocumentReference> getFutureDriverJourneyRefs() throws ExecutionException, InterruptedException;

    void restoreDriverJourney(String djId) throws ExecutionException, InterruptedException;
}
