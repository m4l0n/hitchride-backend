package com.m4l0n.hitchride.service.validations;

import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.pojos.HitchRideUser;

public class RideValidator {

    public String validateCreateRide(HitchRideUser currentLoggedInUser, Ride ride) {
        StringBuilder errors = new StringBuilder();

        this.validateRideParties(errors, ride.getRidePassenger(), ride.getRideDriverJourney().getDjDriver());
        this.validatePassenger(errors, currentLoggedInUser, ride.getRidePassenger());

        return errors.toString();
    }

    public String validateCancelRide(Ride ride) {
        StringBuilder errors = new StringBuilder();

        this.validateRideCancelTimestamp(errors, ride);

        return errors.toString();
    }

    private void validateRideParties(StringBuilder errors, HitchRideUser passenger, HitchRideUser driver) {
        if (passenger.equals(driver)) {
            errors.append("Passenger and driver cannot be the same person.  ");
        }
    }

    private void validatePassenger(StringBuilder errors, HitchRideUser currentLoggedInUser, HitchRideUser passenger) {
        if (!currentLoggedInUser.equals(passenger)) {
            errors.append("Passenger must be the current logged in user. ");
        }
    }

    private void validateRideCancelTimestamp(StringBuilder errors, Ride ride) {
        long fiveMinutesTimestamp = 60 * 5 * 1000L;
        if (ride.getRideDriverJourney().getDjTimestamp() - System.currentTimeMillis() < fiveMinutesTimestamp) {
            errors.append("Ride cannot be cancelled within 5 minutes of the ride start time. ");
        }
    }
}
