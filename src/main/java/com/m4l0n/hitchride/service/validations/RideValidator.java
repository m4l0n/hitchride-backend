package com.m4l0n.hitchride.service.validations;

import com.m4l0n.hitchride.pojos.Ride;

public class RideValidator {

    public String validateCreateRide(String currentLoggedInUser, Ride ride, String driver) {
        StringBuilder errors = new StringBuilder();

        this.validateRideParties(errors, ride.getRidePassenger(), driver);
        this.validatePassenger(errors, currentLoggedInUser, ride.getRidePassenger());

        return errors.toString();
    }

    public String validateCancelRide(Ride ride, Long rideTimestamp) {
        StringBuilder errors = new StringBuilder();

        this.validateRideCancelTimestamp(errors, rideTimestamp);

        return errors.toString();
    }

    private void validateRideParties(StringBuilder errors, String passenger, String driver) {
        if (passenger.equals(driver)) {
            errors.append("Passenger and driver cannot be the same person.  ");
        }
    }

    private void validatePassenger(StringBuilder errors, String currentLoggedInUser, String passenger) {
        if (!currentLoggedInUser.equals(passenger)) {
            errors.append("Passenger must be the current logged in user. ");
        }
    }

    private void validateRideCancelTimestamp(StringBuilder errors, Long rideTimestamp) {
        long fiveMinutesTimestamp = 60 * 5 * 1000L;
        if (rideTimestamp - System.currentTimeMillis() < fiveMinutesTimestamp) {
            errors.append("Ride cannot be cancelled within 5 minutes of the ride start time. ");
        }
    }
}
