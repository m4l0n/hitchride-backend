package com.m4l0n.hitchride.service.impl;

// Programmer's Name: Ang Ru Xian
// Program Name: DriverJourneyServiceImpl.java
// Description: Implementation of DriverJourneyService interface, which contains methods to create, search and accept driver journeys
// Last Modified: 22 July 2023

import com.google.cloud.firestore.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.dto.SearchRideCriteriaDTO;
import com.m4l0n.hitchride.enums.DJStatus;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.DriverJourneyMapper;
import com.m4l0n.hitchride.mapping.SearchRideCriteriaMapper;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import com.m4l0n.hitchride.pojos.OriginDestination;
import com.m4l0n.hitchride.pojos.SearchRideCriteria;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.RideService;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.DriverJourneyValidator;
import com.m4l0n.hitchride.utility.GoogleMapsApiClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DriverJourneyServiceImpl implements DriverJourneyService {

    private final CollectionReference driverJourneyRef;
    private final DriverJourneyValidator driverJourneyValidator;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final GoogleMapsApiClient googleMapsApiClient;
    private final DriverJourneyMapper driverJourneyMapper;
    private final SearchRideCriteriaMapper searchRideCriteriaMapper;
    private final RideService rideService;


    public DriverJourneyServiceImpl(Firestore firestore,
                                    AuthenticationService authenticationService,
                                    UserService userService,
                                    GoogleMapsApiClient googleMapsApiClient,
                                    DriverJourneyMapper driverJourneyMapper,
                                    SearchRideCriteriaMapper searchRideCriteriaMapper,
                                    @Lazy RideService rideService) {
        this.driverJourneyRef = firestore.collection("driver_journey");
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.googleMapsApiClient = googleMapsApiClient;
        this.driverJourneyMapper = driverJourneyMapper;
        this.searchRideCriteriaMapper = searchRideCriteriaMapper;
        this.rideService = rideService;
        driverJourneyValidator = new DriverJourneyValidator();
    }

    @Override
    public DriverJourneyDTO createDriverJourney(DriverJourneyDTO driverJourneyDTO) throws ExecutionException, InterruptedException {
        String authenticatedUsername = authenticationService.getAuthenticatedUsername();
        HitchRideUser currentLoggedInUser = userService.loadUserByUsername(authenticatedUsername);
        DriverJourneyDTO tempDriverJourneyDTO = new DriverJourneyDTO(
                driverJourneyDTO.djId(),
                currentLoggedInUser,
                driverJourneyDTO.djTimestamp(),
                driverJourneyDTO.djOriginDestination(),
                driverJourneyDTO.djDestinationRange(),
                driverJourneyDTO.djPrice()
        );
        DriverJourney driverJourney = driverJourneyMapper.mapDtoToPojo(tempDriverJourneyDTO);
        String errors = driverJourneyValidator.validateCreateDriverJourney(driverJourney, currentLoggedInUser);
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }
        driverJourney.setDjDriver(null);
        driverJourney.setDjStatus(DJStatus.ACTIVE);
        String docId = driverJourneyRef.document()
                .getId();
        driverJourney.setDjId(docId);
        // Create a document reference that references the user document
        DocumentReference documentReference = userService.getUserDocumentReference(authenticatedUsername);

        driverJourneyRef.document(docId)
                .set(driverJourney)
                .get();

        driverJourneyRef.document(docId)
                .update("djDriver", documentReference)
                .get();

        return driverJourneyDTO;
    }

    @Override
    public void acceptDriverJourney(String driverJourneyId, @NonNull Transaction transaction) {
        DocumentReference driverJourneyRef = getDriverJourneyRefById(driverJourneyId);
        transaction.update(driverJourneyRef, "djStatus", DJStatus.ACCEPTED);
    }

    @Override
    @Async
    public CompletableFuture<List<DriverJourneyDTO>> searchRidesFromDriverJourneys(SearchRideCriteriaDTO searchRideCriteriaDTO) throws Exception {
        SearchRideCriteria searchRideCriteria = searchRideCriteriaMapper.mapDtoToPojo(searchRideCriteriaDTO);
        CompletableFuture<List<DriverJourneyDTO>> result = new CompletableFuture<>();
        // Get all driver journeys from the future first
        List<DriverJourney> driverJourneys = getDriverJourneysWithTimestamp(searchRideCriteria.getSearchRideTimestampCriteria());
        // If no future driver journeys, return empty list
        if (driverJourneys.isEmpty()) {
            result.complete(Collections.emptyList());
            return result;
        }
        List<DriverJourneyDTO> suitableRides = Collections.synchronizedList(new ArrayList<>());
        // Create a counter to keep track of the number of driver journeys left
        AtomicInteger counter = new AtomicInteger(driverJourneys.size());

        for (DriverJourney driverJourney : driverJourneys) {
            // First calculate the distance between the origin of the driver journey and the search ride criteria
            googleMapsApiClient.getDistance(driverJourney.getDjOriginDestination()
                            .getOrigin(), searchRideCriteria.getSearchRideLocationCriteria()
                            .getOrigin())
                    .thenAccept(originDistance -> {
                        // Then calculate the distance between the destination of the driver journey and the search ride criteria
                        googleMapsApiClient.getDistance(driverJourney.getDjOriginDestination()
                                        .getDestination(), searchRideCriteria.getSearchRideLocationCriteria()
                                        .getDestination())
                                .thenAccept(destinationDistance -> {
                                    // If the distance between the origin of the driver journey and the search ride criteria is less than 1.0 km
                                    // and the distance between the destination of the driver journey and the search ride criteria is less than the
                                    // destination range of the driver journey, then add the driver journey to the list of suitable rides
                                    if (originDistance <= 1.0 && destinationDistance <= driverJourney.getDjDestinationRange()) {
                                        DriverJourneyDTO driverJourneyDTO = driverJourneyMapper.mapPojoToDto(driverJourney);
                                        suitableRides.add(driverJourneyDTO);
                                    }

                                    if (counter.decrementAndGet() == 0) {
                                        // The counter is 0, which means all driver journeys have been processed
                                        result.complete(suitableRides);
                                    }
                                });
                    });
        }

        return result;
    }

    @Override
    public void deleteDriverJourney(String djId) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        RideDTO rideDTO = rideService.getRideByDriverJourney(djId);
        if (rideDTO != null) {
            throw new HitchrideException("Cannot delete driver journey with active booking.");
        }

//        String errors = driverJourneyValidator.validateDeleteDriverJourney(driverJourney, currentLoggedInUser);
//        if (!errors.isEmpty()) {
//            throw new HitchrideException(errors);
//        }

        updateDriverJourneyStatus(djId, DJStatus.CANCELLED);
        rideService.deleteRideByDriverJourney(djId);

    }

    @Override
    public List<DriverJourneyDTO> getUserDriverJourneys() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        DocumentReference documentReference = userService.getUserDocumentReference(currentLoggedInUser);
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef
                .whereEqualTo("djDriver", documentReference)
                .whereIn("djStatus", List.of(DJStatus.ACTIVE.toString(), DJStatus.ACCEPTED.toString()))
                .whereGreaterThanOrEqualTo("djTimestamp", System.currentTimeMillis())
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        return queryDocumentSnapshots.getDocuments()
                .stream()
//                .filter(documentSnapshot -> DJStatus.valueOf((String) documentSnapshot.get("djStatus")) != DJStatus.CANCELLED)
                .map(documentSnapshot -> driverJourneyMapper.mapPojoToDto(mapDocumentToPojo(documentSnapshot)))
                .collect(Collectors.toList());
    }

    @Override
    public DriverJourney getDriverJourneyById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = getDriverJourneyRefById(id)
                .get()
                .get();
        if (!documentSnapshot.exists()) {
            return null;
        }
        return mapDocumentToPojo(documentSnapshot);
    }

    @Override
    public DocumentReference getDriverJourneyRefById(String id) {
        return driverJourneyRef.document(id);
    }

    @Override
    public List<DocumentReference> getDriverJourneyRefsByDriverUserId(String userId) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = userService.getUserDocumentReference(userId);
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef.whereEqualTo("djDriver", documentReference)
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        return queryDocumentSnapshots.getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference)
                .toList();
    }

    @Override
    public List<DocumentReference> getFutureDriverJourneyRefs() throws ExecutionException, InterruptedException {
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef.whereGreaterThanOrEqualTo("djTimestamp", System.currentTimeMillis())
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        return queryDocumentSnapshots.getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference)
                .toList();
    }

    @Override
    public void restoreDriverJourney(String djId) throws ExecutionException, InterruptedException {
        updateDriverJourneyStatus(djId, DJStatus.ACTIVE);
    }

    private DriverJourney mapDocumentToPojo(DocumentSnapshot documentSnapshot) {
        Map<String, Object> objectMap = documentSnapshot.getData();
        Map<String, String> geoPointMap = (Map<String, String>) objectMap.get("djOriginDestination");
        return new DriverJourney(
                (String) objectMap.get("djId"),
                ((DocumentReference) objectMap.get("djDriver")).getId(),
                ((Number) objectMap.get("djTimestamp")).longValue(),
                new OriginDestination(
                        geoPointMap.get("origin"),
                        geoPointMap.get("destination")
                ),
                ((Number) objectMap.get("djDestinationRange")).intValue(),
                (String) objectMap.get("djPrice"),
                DJStatus.valueOf((String) objectMap.get("djStatus"))
        );
    }

    private List<DriverJourney> getDriverJourneysWithTimestamp(Long timestamp) throws ExecutionException, InterruptedException {
        long fifteenMinutesInMillis = 15 * 60 * 1000;
        //Show results from 15 minutes before and after the timestamp criteria,
        // but also only show results that are in the future
        long startTime = timestamp - fifteenMinutesInMillis;
        long endTime = timestamp + fifteenMinutesInMillis;
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef
                .whereEqualTo("djStatus", DJStatus.ACTIVE)
                .whereGreaterThanOrEqualTo("djTimestamp", System.currentTimeMillis())
                .whereGreaterThanOrEqualTo("djTimestamp", startTime)
                .whereLessThanOrEqualTo("djTimestamp", endTime)
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        return queryDocumentSnapshots.getDocuments()
                .stream()
                .map(this::mapDocumentToPojo)
                .toList();
    }

    private void updateDriverJourneyStatus(String djId, DJStatus djStatus) throws ExecutionException, InterruptedException {
        driverJourneyRef.document(djId)
                .update("djStatus", djStatus)
                .get();
    }

}
