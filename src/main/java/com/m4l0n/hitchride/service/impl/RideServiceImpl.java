package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.m4l0n.hitchride.dto.RideDTO;
import com.m4l0n.hitchride.enums.DJStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class RideServiceImpl implements RideService {

    private final CollectionReference rideRef;
    private final CollectionReference lockRef;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final DriverJourneyService driverJourneyService;
    private final NotificationService notificationService;
    private final RideValidator rideValidator;
    private final RideMapper rideMapper;
    private final DriverJourneyMapper driverJourneyMapper;
    private final Firestore firestore;


    public RideServiceImpl(Firestore firestore, AuthenticationService authenticationService, UserService userService, @Lazy DriverJourneyService driverJourneyService, NotificationService notificationService, RideMapper rideMapper, DriverJourneyMapper driverJourneyMapper, Firestore firestore1) {
        this.rideRef = firestore.collection("rides");
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.driverJourneyService = driverJourneyService;
        this.notificationService = notificationService;
        this.rideMapper = rideMapper;
        this.driverJourneyMapper = driverJourneyMapper;
        this.firestore = firestore;
        rideValidator = new RideValidator();
        lockRef = firestore.collection("locks");
    }

    @Override
    public List<RideDTO> getRecentRides() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        DocumentReference userRef = userService.getUserDocumentReference(currentLoggedInUser);

        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("ridePassenger", userRef)
                .whereEqualTo("rideStatus", RideStatus.COMPLETED.toString())
                .limit(5)
                .get();
        return getRideDTOS(querySnapshot);
    }

    @Override
    public RideDTO bookRide(RideDTO rideDTO) throws ExecutionException, InterruptedException, FirebaseMessagingException, TimeoutException {
        HitchRideUser passenger = getPassengerDetails();
        Ride ride = populateDTOAndCreateRide(rideDTO, passenger);
        String driverUserId = driverJourneyService.getDriverJourneyById(ride.getRideDriverJourney())
                .getDjDriver();
        String errors = rideValidator.validateCreateRide(passenger.getUserId(),
                ride,
                driverUserId);

        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }

        boolean acceptedDriverJourney = bookRideAndAcceptDriverJourney(ride);

        if (acceptedDriverJourney) {
            notifyDriver(driverUserId, ride.getRideId());
            return rideDTO;
        } else {
            throw new HitchrideException("Driver journey is no longer available");
        }
    }

    @Override
    public List<RideDTO> getRecentDrives() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();

        List<DocumentReference> driverJourneyRefs = driverJourneyService.getDriverJourneyRefsByDriverUserId(currentLoggedInUser);

        ApiFuture<QuerySnapshot> querySnapshot = rideRef
                .whereEqualTo("rideStatus", RideStatus.COMPLETED.toString())
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
        // Restore driver journey status back to active
        driverJourneyService.restoreDriverJourney(ride.getRideDriverJourney());

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

        //award users 50 points for completing a ride
        userService.updateUserPoints(rideDTO.ridePassenger()
                .getUserId(), 50);

        return rideDTO;
    }

    @Override
    public List<DocumentReference> getRideRefsByDriver(String driverId) throws ExecutionException, InterruptedException {
        List<DocumentReference> driverJourneyRefs = driverJourneyService.getDriverJourneyRefsByDriverUserId(driverId);
        if (driverJourneyRefs.isEmpty()) {
            return new ArrayList<>();
        }
        QuerySnapshot querySnapshot = rideRef
                .whereIn("rideDriverJourney", driverJourneyRefs)
                .get()
                .get();

        return querySnapshot.getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference)
                .toList();
    }

    @Override
    public List<DocumentReference> getRideRefsByPassenger(String passengerId) throws ExecutionException, InterruptedException {
        DocumentReference userRef = userService.getUserDocumentReference(passengerId);

        QuerySnapshot querySnapshot = rideRef
                .whereEqualTo("ridePassenger", userRef)
                .get()
                .get();

        return querySnapshot.getDocuments()
                .stream()
                .map(DocumentSnapshot::getReference)
                .toList();
    }

    private HitchRideUser getPassengerDetails() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        return userService.loadUserByUsername(currentLoggedInUser);
    }

    private Ride populateDTOAndCreateRide(RideDTO rideDTO, HitchRideUser passenger) {
        String rideId = rideRef.document()
                .getId();

        RideDTO tempDTO = new RideDTO(
                rideId,
                passenger,
                rideDTO.rideOriginDestination(),
                null
        );
        Ride ride = rideMapper.mapDtoToPojo(tempDTO);
        ride.setRideDriverJourney(rideDTO.rideDriverJourney()
                .djId());
        ride.setRidePassenger(passenger.getUserId());
        ride.setRideStatus(RideStatus.ACTIVE);
        return ride;
    }

    private boolean bookRideAndAcceptDriverJourney(Ride ride) throws InterruptedException, ExecutionException, TimeoutException {
        // Create a lock to ensure mutual exclusion
        String lockId = UUID.randomUUID()
                .toString();
        lockRef
                .document(lockId)
                .set(new HashMap<>(), SetOptions.merge());

        boolean acceptedDriverJourney = handleTransaction(ride, lockId);

        lockRef
                .document(lockId)
                .delete();

        return acceptedDriverJourney;
    }

    private boolean handleTransaction(Ride ride, String lockId) throws InterruptedException, ExecutionException, TimeoutException {
        log.info("Starting transaction for booking ride: {}", ride.getRideId());
        // Start a transaction to book the ride and accept the driver journey
        return firestore.runTransaction(transaction -> {
                    // Get lock document and check if it exists
                    DocumentSnapshot lockSnapshot = transaction.get(lockRef
                                    .document(lockId))
                            .get();
                    if (!lockSnapshot.exists()) {
                        return false;
                    }

                    // Check if driver journey is still available
                    DocumentReference driverJourneyRef = driverJourneyService.getDriverJourneyRefById(ride.getRideDriverJourney());
                    DocumentSnapshot driverJourneySnapshot = transaction.get(driverJourneyRef)
                            .get();

                    // Check if driver journey is still available
                    if (DJStatus.valueOf((String) driverJourneySnapshot.get("djStatus")) != DJStatus.ACTIVE) {
                        log.error("Driver journey is no longer available for ID: {}", ride.getRideDriverJourney());
                        return false;
                    }

                    // If still available, update driver journey status
                    driverJourneyService.acceptDriverJourney(ride.getRideDriverJourney(), transaction);

                    // Convert ride object to map and save it to Firestore
                    Gson gson = new Gson();
                    Map<String, Object> rideMap = gson.fromJson(gson.toJson(ride), new TypeToken<>() {
                    }.getType());
                    DocumentReference passengerRef = userService.getUserDocumentReference(ride.getRidePassenger());
                    rideMap.put("ridePassenger", passengerRef);
                    rideMap.put("rideDriverJourney", driverJourneyRef);
                    rideRef.document(ride.getRideId())
                            .set(rideMap);

                    return true;
                })
                .get(5, TimeUnit.SECONDS);
    }

    private void notifyDriver(String driverId, String rideId) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        notificationService.sendNotification(driverId,
                "New Ride Booking",
                "A user has booked your ride!",
                "common",
                rideId);
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
