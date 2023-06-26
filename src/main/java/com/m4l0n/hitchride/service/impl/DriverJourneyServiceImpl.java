package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElementStatus;
import com.google.maps.model.LatLng;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.SearchRideCriteria;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.DriverJourneyValidator;
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
    private final GeoApiContext geoApiContext;

    public DriverJourneyServiceImpl(Firestore firestore, AuthenticationService authenticationService, GeoApiContext geoApiContext) {
        this.driverJourneyRef = firestore.collection("driver_journey");
        this.authenticationService = authenticationService;
        this.geoApiContext = geoApiContext;
        driverJourneyValidator = new DriverJourneyValidator();
    }

    @Override
    public DriverJourney createDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException {
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

        return driverJourney;
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
    public CompletableFuture<List<DriverJourney>> searchRidesFromDriverJourneys(SearchRideCriteria searchRideCriteria) throws Exception {
        CompletableFuture<List<DriverJourney>> result = new CompletableFuture<>();
        List<DriverJourney> driverJourneys = getDriverJourneysWithTimestamp(searchRideCriteria.getSearchRideTimestampCriteria());
        if (driverJourneys.isEmpty()) {
            result.complete(Collections.emptyList());
            return result;
        }
        List<DriverJourney> suitableRides = Collections.synchronizedList(new ArrayList<>());

        AtomicInteger counter = new AtomicInteger(driverJourneys.size());

        for (DriverJourney driverJourney : driverJourneys) {
            getDistance(driverJourney.getDjLocationData()
                    .getOrigin(), searchRideCriteria.getSearchRideLocationCriteria().getOrigin()).thenAccept(originDistance -> {
                getDistance(driverJourney.getDjLocationData()
                        .getDestination(), searchRideCriteria.getSearchRideLocationCriteria().getDestination()).thenAccept(destinationDistance -> {
                    if (originDistance <= 1.0 && destinationDistance <= driverJourney.getDjDestinationRange()) {
                        suitableRides.add(driverJourney);
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
    public DriverJourney deleteDriverJourney(DriverJourney driverJourney) throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        String errors = driverJourneyValidator.validateDeleteDriverJourney(driverJourney, currentLoggedInUser);
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        if (executeDeleteDriverJourney(driverJourney.getDjId())) {
            return driverJourney;
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
    public List<DriverJourney> getUserDriverJourneys() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef.whereEqualTo("djDriver.userId", currentLoggedInUser)
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        return queryDocumentSnapshots.toObjects(DriverJourney.class);
    }

    private List<DriverJourney> getDriverJourneysWithTimestamp(Long timestamp) throws ExecutionException, InterruptedException {
        long tenMinutesInMillis = 10 * 60 * 1000;
        long startTime = timestamp - tenMinutesInMillis;
        long endTime = timestamp + tenMinutesInMillis;
        QuerySnapshot queryDocumentSnapshots = driverJourneyRef.whereGreaterThanOrEqualTo("djTimestamp", startTime)
                .whereLessThanOrEqualTo("djTimestamp", endTime)
                .get()
                .get();
        if (queryDocumentSnapshots.isEmpty()) {
            return List.of();
        }
        return queryDocumentSnapshots.toObjects(DriverJourney.class);
    }

    private CompletableFuture<Double> getDistance(GeoPoint point1, GeoPoint point2) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        LatLng origin = new LatLng(point1.getLatitude(), point1.getLongitude());
        LatLng destination = new LatLng(point2.getLatitude(), point2.getLongitude());

        DistanceMatrixApi.newRequest(geoApiContext)
                .origins(origin)
                .destinations(destination)
                .setCallback(new PendingResult.Callback<DistanceMatrix>() {
                    @Override
                    public void onResult(DistanceMatrix result) {
                        if (result.rows.length > 0 && result.rows[0].elements.length > 0 && result.rows[0].elements[0].status == DistanceMatrixElementStatus.OK) {
                            future.complete(result.rows[0].elements[0].distance.inMeters / 1000.0); // Convert meters to kilometers
                        } else {
                            future.completeExceptionally(new Exception("Failed to calculate distance"));
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        future.completeExceptionally(e);
                    }
                });

        return future;
    }

}
