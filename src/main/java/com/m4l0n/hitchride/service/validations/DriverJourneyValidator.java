package com.m4l0n.hitchride.service.validations;

import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.pojos.DriverJourney;

public class DriverJourneyValidator {

    public String validateCreateDriverJourney(DriverJourney driverJourney) {
        StringBuilder errors = new StringBuilder();
        this.validateDriverJourneyLocation(errors, driverJourney.getDjOrigin(), driverJourney.getDjDestination());

        return errors.toString();
    }

    private void validateDriverJourneyLocation(StringBuilder errors, GeoPoint driverJourneyOrigin, GeoPoint driverJourneyDestination) {
        if (driverJourneyOrigin.equals(driverJourneyDestination))
            errors.append("Origin and destination cannot be the same.");
    }

}
