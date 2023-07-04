package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.enums.RideStatus;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.DriverJourneyMapper;
import com.m4l0n.hitchride.mapping.RideMapper;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import com.m4l0n.hitchride.pojos.OriginDestination;
import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.NotificationService;
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
    private final NotificationService notificationService;
    private final RideValidator rideValidator;
    private final RideMapper rideMapper;
    private final DriverJourneyMapper driverJourneyMapper;


    public RideServiceImpl(Firestore firestore, AuthenticationService authenticationService, UserService userService, @Lazy DriverJourneyService driverJourneyService, NotificationService notificationService, RideMapper rideMapper, DriverJourneyMapper driverJourneyMapper) {
        this.rideRef = firestore.collection("rides");
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.driverJourneyService = driverJourneyService;
        this.notificationService = notificationService;
        this.rideMapper = rideMapper;
        this.driverJourneyMapper = driverJourneyMapper;
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
    public RideDTO bookRide(RideDTO rideDTO) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        //Fill ride object
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        HitchRideUser passenger = userService.loadUserByUsername(currentLoggedInUser);
        DriverJourney driverJourney = driverJourneyService.getDriverJourneyById(rideDTO.rideDriverJourney()
                .djId());
        String rideId = rideRef.document()
                .getId();
        RideDTO tempDTO = new RideDTO(
                rideId,
                passenger,
                rideDTO.rideOriginDestination(),
                driverJourneyMapper.mapPojoToDto(driverJourney)
        );
        Ride ride = rideMapper.mapDtoToPojo(tempDTO);
        ride.setRideId(rideId);
        ride.setRidePassenger(currentLoggedInUser);
        ride.setRideStatus(RideStatus.ACTIVE);

        String errors = rideValidator.validateCreateRide(currentLoggedInUser, ride, driverJourney.getDjDriver());
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        //Notify driver
        notificationService.sendNotification(driverJourney.getDjDriver(),
                "New Ride Booking",
                "A user has booked your ride!",
                "common",
                rideDTO.rideId());

        //Save ride in Firestore
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
                .whereEqualTo("rideStatus", RideStatus.COMPLETED)
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
                .whereEqualTo("rideStatus", RideStatus.ACTIVE)
                .whereEqualTo("ridePassenger", userRef)
                .whereIn("rideDriverJourney", driverJourneyRefs)
                .get();
        return getRideDTOS(querySnapshot);
    }

    @Override
    public Boolean cancelRide(String rideId) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        Ride ride = getRideById(rideId);
        DriverJourney driverJourney = driverJourneyService.getDriverJourneyById(ride.getRideDriverJourney());
        String errors = rideValidator.validateCancelRide(ride, driverJourney.getDjTimestamp());
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        rideRef.document(ride.getRideId())
                .delete()
                .get();

        DocumentSnapshot documentSnapshot = rideRef.document(ride.getRideId())
                .get()
                .get();

        notificationService.sendNotification(driverJourney.getDjDriver(),
                "Ride Cancelled",
                "A user has cancelled your ride!",
                "common");

        return !documentSnapshot.exists();
    }

    @Override
    public Ride getRideById(String rideId) throws ExecutionException, InterruptedException {
        DocumentSnapshot documentSnapshot = rideRef.document(rideId)
                .get()
                .get();
        if (documentSnapshot.exists()) {
            return mapDocumentToRide(documentSnapshot);
        }
        return null;
    }

    @Override
    public DocumentReference getRideReferenceById(String rideId) throws ExecutionException, InterruptedException {
        return rideRef.document(rideId);
    }

    @Override
    public Boolean deleteRideByDriverJourney(String driverJourneyId) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("rideDriverJourney", driverJourneyService.getDriverJourneyRefById(driverJourneyId))
                .get();
        List<RideDTO> rides = getRideDTOS(querySnapshot);
        if (rides.size() == 1) {
            rideRef.document(rides.get(0)
                            .rideId())
                    .delete()
                    .get();
            notificationService.sendNotification(rides.get(0)
                    .ridePassenger()
                    .getUserId(), "Ride Cancelled", "Your ride has been cancelled!", "common");
        }
        return true;
    }

    @Override
    public RideDTO getRideByDriverJourney(String driverJourneyId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("rideDriverJourney", driverJourneyService.getDriverJourneyRefById(driverJourneyId))
                .get();
        List<RideDTO> rides = getRideDTOS(querySnapshot);
        if (rides.size() == 1) {
            return rides.get(0);
        }
        return null;
    }

    @Override
    public RideDTO completeRide(RideDTO rideDTO) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        rideRef.document(rideDTO.rideId())
                .update("rideStatus", RideStatus.COMPLETED)
                .get();

        notificationService.sendNotification(rideDTO.ridePassenger()
                .getUserId(), "Ride Completed", "Your ride has been completed!", "review");

        return rideDTO;
    }

    private Ride mapDocumentToRide(DocumentSnapshot documentSnapshot) {
        Map<String, Object> documentData = documentSnapshot.getData();
        DocumentReference userRef = documentSnapshot.get("ridePassenger", DocumentReference.class);
        DocumentReference djRef = documentSnapshot.get("rideDriverJourney", DocumentReference.class);
        Map<String, String> geoPointMap = (Map<String, String>) documentData.get("rideOriginDestination");
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
