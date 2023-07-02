package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.m4l0n.hitchride.dto.DriverJourneyDTO;
import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.RideMapper;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import com.m4l0n.hitchride.pojos.OriginDestination;
import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.RideService;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.RideValidator;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class RideServiceImpl implements RideService {

    private final CollectionReference rideRef;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final DriverJourneyService driverJourneyService;
    private final RideValidator rideValidator;
    private final RideMapper rideMapper;


    public RideServiceImpl(Firestore firestore, AuthenticationService authenticationService, UserService userService, @Lazy DriverJourneyService driverJourneyService, RideMapper rideMapper) {
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

        DocumentReference userRef = userService.getUserDocumentReference(currentLoggedInUser);

        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("ridePassenger", userRef)
                .limit(5)
                .get();
        return getRideDTOS(querySnapshot);
    }

    @Override
    public RideDTO acceptRide(RideDTO rideDTO) throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        HitchRideUser passenger = userService.loadUserByUsername(currentLoggedInUser);
        DriverJourneyDTO driverJourneyDTO = driverJourneyService.getDriverJourneyById(rideDTO.rideDriverJourney()
                .djId());
        String rideId = rideRef.document()
                .getId();
        RideDTO tempDTO = new RideDTO(
                rideId,
                passenger,
                rideDTO.rideOriginDestination(),
                driverJourneyDTO
        );
        Ride ride = rideMapper.mapDtoToPojo(tempDTO);
        ride.setRideId(rideId);
        ride.setRidePassenger(currentLoggedInUser);

        String errors = rideValidator.validateCreateRide(currentLoggedInUser, ride, driverJourneyDTO.djDriver()
                .getUserId());

        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        DocumentReference userRef = userService.getUserDocumentReference(ride.getRidePassenger());
        DocumentReference djRef = driverJourneyService.getDriverJourneyRefById(ride.getRideDriverJourney());

        rideRef.document(rideId)
                .set(ride)
                .get();
        rideRef.document(rideId)
                .update("ridePassenger", userRef, "rideDriverJourney", djRef)
                .get();

        boolean acceptedDriverJourney = driverJourneyService.acceptDriverJourney(ride.getRideDriverJourney());
        if (acceptedDriverJourney) {
            return rideDTO;
        }
        return null;
    }

    @Override
    public List<RideDTO> getRecentDrives() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        List<DocumentReference> driverJourneyRefs = driverJourneyService.getDriverJourneyRefsByDriverUserId(currentLoggedInUser);

        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereIn("rideDriverJourney", driverJourneyRefs)
                .limit(5)
                .get();

        return getRideDTOS(querySnapshot);
    }

    @Override
    public List<RideDTO> getUpcomingRides() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        DocumentReference userRef = userService.getUserDocumentReference(currentLoggedInUser);
        List<DocumentReference> driverJourneyRefs = driverJourneyService.getFutureDriverJourneyRefs();

        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("ridePassenger", userRef)
                .whereIn("rideDriverJourney", driverJourneyRefs)
                .get();
        return getRideDTOS(querySnapshot);
    }

    @Override
    public Boolean cancelRide(RideDTO rideDTO) throws ExecutionException, InterruptedException {
        Ride ride = rideMapper.mapDtoToPojo(rideDTO);
        String errors = rideValidator.validateCancelRide(ride, rideDTO.rideDriverJourney()
                .djTimestamp());
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        rideRef.document(ride.getRideId())
                .delete()
                .get();

        DocumentSnapshot documentSnapshot = rideRef.document(ride.getRideId())
                .get()
                .get();

        return !documentSnapshot.exists();
    }

    @Override
    public RideDTO getRideById(String rideId) throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = rideRef.document(rideId)
                .get()
                .get();
        if (documentSnapshot.exists()) {
            return rideMapper.mapPojoToDto(mapDocumentToRide(documentSnapshot));
        }
        return null;
    }

    @Override
    public DocumentReference getRideReferenceById(String rideId) throws ExecutionException, InterruptedException {
        return rideRef.document(rideId);
    }

    @Override
    public Boolean deleteRideByDriverJourney(String driverJourneyId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("rideDriverJourney", driverJourneyService.getDriverJourneyRefById(driverJourneyId))
                .get();
        List<RideDTO> rides = getRideDTOS(querySnapshot);
        if (rides.size() == 1) {
            rideRef.document(rides.get(0)
                    .rideId())
                    .delete()
                    .get();
        }
        return true;
    }

    @Override
    public RideDTO getRideByDriverJourney(String driverJourneyId) throws ExecutionException, InterruptedException{
        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("rideDriverJourney", driverJourneyService.getDriverJourneyRefById(driverJourneyId))
                .get();
        List<RideDTO> rides = getRideDTOS(querySnapshot);
        if (rides.size() == 1) {
            return rides.get(0);
        }
        return null;
    }

    private Ride mapDocumentToRide(DocumentSnapshot documentSnapshot) {
        Map<String, Object> documentData = documentSnapshot.getData();
        DocumentReference userRef = documentSnapshot.get("ridePassenger", DocumentReference.class);
        DocumentReference djRef = documentSnapshot.get("rideDriverJourney", DocumentReference.class);
        Map<String, GeoPoint> geoPointMap = (Map<String, GeoPoint>) documentData.get("rideOriginDestination");
        return new Ride(
                (String) documentData.get("rideId"),
                userRef.getId(),
                new OriginDestination(
                        geoPointMap.get("origin"),
                        geoPointMap.get("destination")
                ),
                djRef.getId()
        );
    }

    @NonNull
    private List<RideDTO> getRideDTOS(ApiFuture<QuerySnapshot> querySnapshot) throws InterruptedException, ExecutionException {
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            return document.getDocuments()
                    .stream()
                    .map(this::mapDocumentToRide)
                    .map(rideMapper::mapPojoToDto)
                    .sorted((r1, r2) -> r2.rideDriverJourney()
                            .djTimestamp()
                            .compareTo(r1.rideDriverJourney()
                                    .djTimestamp()))
                    .toList();
        }
        return List.of();
    }

}
