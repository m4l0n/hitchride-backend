package com.m4l0n.hitchride.dto;

import com.m4l0n.hitchride.pojos.HitchRideUser;

public record RideDTO(
        String rideId,
        HitchRideUser ridePassenger,
        OriginDestinationDTO rideOriginDestination,
        DriverJourneyDTO rideDriverJourney
) {
}
