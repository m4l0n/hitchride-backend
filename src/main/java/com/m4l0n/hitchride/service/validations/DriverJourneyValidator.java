package com.m4l0n.hitchride.service.validations;

import com.google.cloud.firestore.GeoPoint;
import com.m4l0n.hitchride.pojos.DriverInfo;
import com.m4l0n.hitchride.pojos.DriverJourney;
import io.micrometer.common.util.StringUtils;

public class DriverJourneyValidator {

    public String validateCreateDriverJourney(DriverJourney driverJourney) {
        StringBuilder errors = new StringBuilder();
        this.validateDriverJourneyLocation(errors, driverJourney.getDjOriginDestination()
                .getOrigin(), driverJourney.getDjOriginDestination()
                .getDestination());
        this.validateDriverJourneyPrice(errors, driverJourney.getDjPrice());
        this.validateDriverInfoExists(errors, driverJourney.getDjDriver()
                .getUserDriverInfo());

        return errors.toString();
    }

    public String validateDeleteDriverJourney(DriverJourney driverJourney, String currentLoggedInUser) {
        StringBuilder errors = new StringBuilder();
        this.validateDriverJourneyDriver(errors, driverJourney.getDjDriver()
                .getUserId(), currentLoggedInUser);

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

    private void validateDriverInfoExists(StringBuilder errors, DriverInfo driverInfo) {
        if (driverInfo == null) {
            errors.append("Driver info must exist. ");
            return;
        }

        if (StringUtils.isEmpty(driverInfo.getDiCarBrand()))
            errors.append("Car brand cannot be empty. ");
        if (StringUtils.isEmpty(driverInfo.getDiCarModel()))
            errors.append("Car model cannot be empty. ");
        if (StringUtils.isEmpty(driverInfo.getDiCarColor()))
            errors.append("Car color cannot be empty. ");
        if (StringUtils.isEmpty(driverInfo.getDiCarLicensePlate()))
            errors.append("Car license plate cannot be empty. ");
        if (driverInfo.getDiDateCarBoughtTimestamp() == null)
            errors.append("Car bought date cannot be empty. ");
        if (driverInfo.getDiDateJoinedTimestamp() == null)
            errors.append("Date joined cannot be empty. ");
        if (driverInfo.getDiIsCarSecondHand() == null)
            errors.append("Is car second hand cannot be empty. ");
    }

}
