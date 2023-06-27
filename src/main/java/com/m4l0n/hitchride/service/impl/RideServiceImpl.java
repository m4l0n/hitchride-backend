package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.RideMapper;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.RideService;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.RideValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class RideServiceImpl implements RideService {

    private final CollectionReference rideRef;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final DriverJourneyService driverJourneyService;
    private final RideValidator rideValidator;
    private final RideMapper rideMapper;


    public RideServiceImpl(Firestore firestore, AuthenticationService authenticationService, UserService userService, DriverJourneyService driverJourneyService, RideMapper rideMapper) {
        this.rideRef = firestore.collection("rides");
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.driverJourneyService = driverJourneyService;
        this.rideMapper = rideMapper;
        rideValidator = new RideValidator();
    }

    @Override
    public List<RideDTO> getRecentRides() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        ApiFuture<QuerySnapshot> querySnapshot = rideRef.orderBy("rideDriverJourney.djTimestamp", Query.Direction.DESCENDING)
                .whereEqualTo("ridePassenger.userId", currentLoggedInUser)
                .limit(5)
                .get();
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            return document.toObjects(Ride.class)
                    .stream()
                    .map(rideMapper::mapPojoToDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public RideDTO acceptRide(RideDTO rideDTO) throws ExecutionException, InterruptedException {
        String currentUserName = authenticationService.getAuthenticatedUsername();
        HitchRideUser currentLoggedInUser = userService.loadUserByUsername(currentUserName);

        Ride ride = rideMapper.mapDtoToPojo(rideDTO);
        String errors = rideValidator.validateCreateRide(currentLoggedInUser, ride);

        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        String rideId = rideRef.document()
                .getId();
        ride.setRideId(rideId);

        rideRef.document(rideId)
                .set(ride)
                .get();

        DriverJourney acceptedDriverJourney = driverJourneyService.acceptDriverJourney(ride.getRideDriverJourney());
        if (acceptedDriverJourney == null) {
            return null;
        }
        return rideDTO;
    }

    @Override
    public List<RideDTO> getRecentDrives() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        ApiFuture<QuerySnapshot> querySnapshot = rideRef.orderBy("rideDriverJourney.djTimestamp", Query.Direction.DESCENDING)
                .whereEqualTo("rideDriverJourney.djDriver.userId", currentLoggedInUser)
                .limit(5)
                .get();
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            return document.toObjects(Ride.class)
                    .stream()
                    .map(rideMapper::mapPojoToDto)
                    .toList();
        }
        return List.of();
    }

}
