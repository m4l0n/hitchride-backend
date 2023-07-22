package com.m4l0n.hitchride.dto;

// Programmer Name: Ang Ru Xian
// Program Name: RideDTO.java
// Description: This is a class that represents the ride data transfer object
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.pojos.HitchRideUser;

public record RideDTO(
        String rideId,
        HitchRideUser ridePassenger,
        OriginDestinationDTO rideOriginDestination,
        DriverJourneyDTO rideDriverJourney
) {
}
