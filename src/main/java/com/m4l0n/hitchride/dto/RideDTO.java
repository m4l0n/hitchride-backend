package com.m4l0n.hitchride.dto;

import com.m4l0n.hitchride.pojos.DriverJourney;

public record RideDTO(
        String rideId,
        UserDTO ridePassenger,
        OriginDestinationDTO rideOriginDestination,
        DriverJourney rideDriverJourney
) {
}
