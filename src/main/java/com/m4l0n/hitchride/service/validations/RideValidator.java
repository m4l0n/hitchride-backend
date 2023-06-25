package com.m4l0n.hitchride.service.validations;

import com.m4l0n.hitchride.pojos.Ride;
import com.m4l0n.hitchride.pojos.User;

public class RideValidator {

    public String validateCreateRide(User currentLoggedInUser, Ride ride) {
        StringBuilder errors = new StringBuilder();

        this.validateRideParties(errors, ride.getRidePassenger(), ride.getRideDriverJourney().getDjDriver());
        this.validatePassenger(errors, currentLoggedInUser, ride.getRidePassenger());

        return errors.toString();
    }

    private void validateRideParties(StringBuilder errors, User passenger, User driver) {
        if (passenger.equals(driver)) {
            errors.append("Passenger and driver cannot be the same person");
        }
    }

    private void validatePassenger(StringBuilder errors, User currentLoggedInUser, User passenger) {
        if (!currentLoggedInUser.equals(passenger)) {
            errors.append("Passenger must be the current logged in user");
        }
    }
}
