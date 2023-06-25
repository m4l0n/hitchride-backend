package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.service.DriverJourneyService;
import com.m4l0n.hitchride.service.validations.DriverJourneyValidator;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class DriverJourneyServiceImpl implements DriverJourneyService {

    private final CollectionReference driverJourneyRef;
    private final DriverJourneyValidator driverJourneyValidator;

    public DriverJourneyServiceImpl(Firestore firestore) {
        this.driverJourneyRef = firestore.collection("driver_journey");
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
        ApiFuture<WriteResult> writeResultApiFuture = driverJourneyRef.document(driverJourney.getDjId())
                .delete();
        writeResultApiFuture.get();

        DocumentSnapshot documentSnapshot = driverJourneyRef.document(driverJourney.getDjId())
                .get()
                .get();

        if (documentSnapshot.exists()) {
            return null;
        }
        return driverJourney;
    }

}
