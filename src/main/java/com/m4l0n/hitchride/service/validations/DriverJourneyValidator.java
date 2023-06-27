package com.m4l0n.hitchride.service.validations;

import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.pojos.DriverJourney;

public class DriverJourneyValidator {

    public String validateCreateDriverJourney(DriverJourney driverJourney) {
        StringBuilder errors = new StringBuilder();
        this.validateDriverJourneyLocation(errors, driverJourney.getDjOriginDestination()
                .getOrigin(), driverJourney.getDjOriginDestination()
                .getDestination());
        this.validateDriverJourneyPrice(errors, driverJourney.getDjPrice());

        return errors.toString();
    }
    
    public String validateDeleteDriverJourney(DriverJourney driverJourney, String currentLoggedInUser) {
        StringBuilder errors = new StringBuilder();
        this.validateDriverJourneyDriver(errors, driverJourney.getDjDriver().getUserId(), currentLoggedInUser);

        return errors.toString();
    }

    private void validateDriverJourneyLocation(StringBuilder errors, GeoPoint driverJourneyOrigin, GeoPoint driverJourneyDestination) {
        if (driverJourneyOrigin.equals(driverJourneyDestination))
            errors.append("Origin and destination cannot be the same. ");
    }
    
    private void validateDriverJourneyDriver(StringBuilder errors, String djDriverId, String currentLoggedInUser) {
        if (!djDriverId.equals(currentLoggedInUser))
            errors.append("Driver must be the current logged in user. ");
    }

    private void validateDriverJourneyPrice(StringBuilder errors, String djPrice) {
        if (djPrice.isEmpty())
            errors.append("Price cannot be empty. ");
        //regex check if string is valid number
        if (!djPrice.matches("\\d+(\\.\\d+)?"))
            errors.append("Price must be a valid number. ");
    }

}
