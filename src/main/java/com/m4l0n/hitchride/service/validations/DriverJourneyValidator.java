package com.m4l0n.hitchride.service.validations;

import com.m4l0n.hitchride.pojos.DriverInfo;
import com.m4l0n.hitchride.pojos.DriverJourney;
import com.m4l0n.hitchride.pojos.HitchRideUser;
import io.micrometer.common.util.StringUtils;

public class DriverJourneyValidator {

    public String validateCreateDriverJourney(DriverJourney driverJourney, HitchRideUser currentLoggedInUser) {
        StringBuilder errors = new StringBuilder();
        this.validateDriverJourneyLocation(errors, driverJourney.getDjOriginDestination()
                .getOrigin(), driverJourney.getDjOriginDestination()
                .getDestination());
        this.validateDriverJourneyCreateTime(errors, driverJourney.getDjTimestamp());
        this.validateDriverJourneyPrice(errors, driverJourney.getDjPrice());
        this.validateDriverInfoExists(errors, currentLoggedInUser);

        return errors.toString();
    }

    public String validateDeleteDriverJourney(DriverJourney driverJourney, HitchRideUser currentLoggedInUser) {
        StringBuilder errors = new StringBuilder();
        this.validateDeleteTime(errors, driverJourney.getDjTimestamp());

        return errors.toString();
    }

    private void validateDriverJourneyCreateTime(StringBuilder errors, Long djTimestamp) {
        long fiveMinutesInMillis = 60 * 5 * 1000L;
        if (djTimestamp < System.currentTimeMillis() + fiveMinutesInMillis)
            errors.append("Driver journey must be created at least 5 minutes before ride. ");
    }

    private void validateDriverJourneyLocation(StringBuilder errors, String driverJourneyOrigin, String driverJourneyDestination) {
        if (driverJourneyOrigin.equals(driverJourneyDestination))
            errors.append("Origin and destination cannot be the same. ");
    }

    private void validateDeleteTime(StringBuilder errors, Long djTimestamp) {
        long tenMinutesInMillis = 60 * 10 * 1000L;
        if (djTimestamp < System.currentTimeMillis() + tenMinutesInMillis)
            errors.append("Driver journey cannot be deleted within 10 minutes of ride.");
    }

    private void validateDriverJourneyPrice(StringBuilder errors, String djPrice) {
        if (djPrice.isEmpty())
            errors.append("Price cannot be empty. ");
        //regex check if string is valid number
        if (!djPrice.matches("\\d+(\\.\\d+)?"))
            errors.append("Price must be a valid number. ");
    }

    private void validateDriverInfoExists(StringBuilder errors, HitchRideUser userInfo) {
        DriverInfo driverInfo = userInfo.getUserDriverInfo();
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
        if (StringUtils.isEmpty(userInfo.getUserPhotoUrl()))
            errors.append("Profile picture cannot be empty. ");
    }

}
