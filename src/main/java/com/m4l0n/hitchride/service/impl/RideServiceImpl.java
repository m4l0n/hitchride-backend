package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.pojos.User;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.RideService;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.RideValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RideServiceImpl implements RideService {

    private final CollectionReference rideRef;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final DriverJourneyService driverJourneyService;
    private final RideValidator rideValidator;



    public RideServiceImpl(Firestore firestore, AuthenticationService authenticationService, UserService userService, DriverJourneyService driverJourneyService) {
        this.rideRef = firestore.collection("rides");
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.driverJourneyService = driverJourneyService;
        rideValidator = new RideValidator();
    }

    @Override
    public List<Ride> getRecentRides() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        List<Ride> rides;

        ApiFuture<QuerySnapshot> querySnapshot = rideRef.orderBy("rideDriverJourney.djTimestamp", Query.Direction.DESCENDING)
                .whereEqualTo("ridePassenger.userId", currentLoggedInUser)
                .limit(5)
                .get();
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            rides = document.toObjects(Ride.class);
        } else {
            rides = List.of();
        }

        return rides;
    }

    @Override
    public Ride acceptRide(Ride ride) throws ExecutionException, InterruptedException {
        String currentUserName = authenticationService.getAuthenticatedUsername();
        User currentLoggedInUser = userService.loadUserByUsername(currentUserName);
        String errors = rideValidator.validateCreateRide(currentLoggedInUser, ride);

        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        String rideId = rideRef.document().getId();
        ride.setRideId(rideId);

        rideRef.document(rideId)
                .set(ride)
                .get();

        DriverJourney acceptedDriverJourney = driverJourneyService.acceptDriverJourney(ride.getRideDriverJourney());
        if (acceptedDriverJourney == null) {
            return null;
        }
        return ride;
    }

    @Override
    public List<Ride> getRecentDrives() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        List<Ride> rides;

        ApiFuture<QuerySnapshot> querySnapshot = rideRef.orderBy("rideDriverJourney.djTimestamp", Query.Direction.DESCENDING)
                .whereEqualTo("rideDriverJourney.djDriver.userId", currentLoggedInUser)
                .limit(5)
                .get();
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            rides = document.toObjects(Ride.class);
        } else {
            rides = List.of();
        }

        return rides;
    }

}
