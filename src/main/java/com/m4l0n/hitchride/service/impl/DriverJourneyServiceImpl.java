package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.dto.SearchRideCriteriaDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.DriverJourneyMapper;
import com.m4l0n.hitchride.mapping.SearchRideCriteriaMapper;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.SearchRideCriteria;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.DriverJourneyValidator;
import com.m4l0n.hitchride.utility.GoogleMapsApiClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DriverJourneyServiceImpl implements DriverJourneyService {

    private final CollectionReference driverJourneyRef;
    private final DriverJourneyValidator driverJourneyValidator;
    private final AuthenticationService authenticationService;
    private final GoogleMapsApiClient googleMapsApiClient;
    private final DriverJourneyMapper driverJourneyMapper;
    private final SearchRideCriteriaMapper searchRideCriteriaMapper;


    public DriverJourneyServiceImpl(Firestore firestore, AuthenticationService authenticationService, GoogleMapsApiClient googleMapsApiClient, DriverJourneyMapper driverJourneyMapper, SearchRideCriteriaMapper searchRideCriteriaMapper) {
        this.driverJourneyRef = firestore.collection("driver_journey");
        this.authenticationService = authenticationService;
        this.googleMapsApiClient = googleMapsApiClient;
        this.driverJourneyMapper = driverJourneyMapper;
        this.searchRideCriteriaMapper = searchRideCriteriaMapper;
        driverJourneyValidator = new DriverJourneyValidator();
    }

    @Override
    public DriverJourneyDTO createDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException {
        DriverJourney driverJourney = driverJourneyMapper.mapDtoToPojo(driverJourneyDTO);
        String errors = driverJourneyValidator.validateCreateDriverJourney(driverJourney);
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        String docId = driverJourneyRef.document()
                .getId();
        driverJourney.setDjId(docId);
        driverJourneyRef.document(docId)
                .set(driverJourney)
                .get();
        return driverJourneyDTO;
    }

    @Override
    public DriverJourney acceptDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException {
        if (executeDeleteDriverJourney(driverJourney.getDjId())) {
            return driverJourney;
        }
        return null;
    }

    @Override
    @Async
    public CompletableFuture<List<DriverJourneyDTO>> searchRidesFromDriverJourneys(SearchRideCriteriaDTO searchRideCriteriaDTO) throws Exception {
        SearchRideCriteria searchRideCriteria = searchRideCriteriaMapper.mapDtoToPojo(searchRideCriteriaDTO);
        CompletableFuture<List<DriverJourneyDTO>> result = new CompletableFuture<>();
        List<DriverJourney> driverJourneys = getDriverJourneysWithTimestamp(searchRideCriteria.getSearchRideTimestampCriteria());
        if (driverJourneys.isEmpty()) {
            result.complete(Collections.emptyList());
            return result;
        }
        List<DriverJourneyDTO> suitableRides = Collections.synchronizedList(new ArrayList<>());

        AtomicInteger counter = new AtomicInteger(driverJourneys.size());

        for (DriverJourney driverJourney : driverJourneys) {
            googleMapsApiClient.getDistance(driverJourney.getDjOriginDestination()
                            .getOrigin(), searchRideCriteria.getSearchRideLocationCriteria()
                            .getOrigin())
                    .thenAccept(originDistance -> {
                        googleMapsApiClient.getDistance(driverJourney.getDjOriginDestination()
                                        .getDestination(), searchRideCriteria.getSearchRideLocationCriteria()
                                        .getDestination())
                                .thenAccept(destinationDistance -> {
                                    if (originDistance <= 1.0 && destinationDistance <= driverJourney.getDjDestinationRange()) {
                                        DriverJourneyDTO driverJourneyDTO = driverJourneyMapper.mapPojoToDto(driverJourney);
                                        suitableRides.add(driverJourneyDTO);
                                    }

                                    if (counter.decrementAndGet() == 0) {
                                        result.complete(suitableRides);
                                    }
                                });
                    });
        }

        return result;
    }

    @Override
    public DriverJourneyDTO deleteDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        DriverJourney driverJourney = driverJourneyMapper.mapDtoToPojo(driverJourneyDTO);
        String errors = driverJourneyValidator.validateDeleteDriverJourney(driverJourney, currentLoggedInUser);
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        if (executeDeleteDriverJourney(driverJourney.getDjId())) {
            return driverJourneyDTO;
        }
        return null;
    }

    private Boolean executeDeleteDriverJourney(String djId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> writeResultApiFuture = driverJourneyRef.document(djId)
                .delete();
        writeResultApiFuture.get();

        DocumentSnapshot documentSnapshot = driverJourneyRef.document(djId)
                .get()
                .get();

        return !documentSnapshot.exists();
    }

    @Override
    public List<DriverJourneyDTO> getUserDriverJourneys() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef.whereEqualTo("djDriver.userId", currentLoggedInUser)
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        List<DriverJourney> driverJourneys = queryDocumentSnapshots.toObjects(DriverJourney.class);
        return driverJourneys.stream()
                .map(driverJourneyMapper::mapPojoToDto)
                .toList();
    }

    private List<DriverJourney> getDriverJourneysWithTimestamp(Long timestamp) throws ExecutionException, InterruptedException {
        long fifteenMinutesInMillis = 15 * 60 * 1000;
        //earliest book time is 15 minutes before the ride
        long startTime = timestamp - fifteenMinutesInMillis;
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef
                .whereLessThanOrEqualTo("djTimestamp", startTime)
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        return queryDocumentSnapshots.toObjects(DriverJourney.class);
    }

}
