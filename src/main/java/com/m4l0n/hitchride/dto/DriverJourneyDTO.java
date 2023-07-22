package com.m4l0n.hitchride.dto;

// Programmer Name: Ang Ru Xian
// Program Name: DriverJourneyDTO.java
// Description: This is a class that represents the driver journey data transfer object
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.pojos.HitchRideUser;

public record DriverJourneyDTO(
        String djId,
        HitchRideUser djDriver,
        Long djTimestamp,
        OriginDestinationDTO djOriginDestination,
        Integer djDestinationRange,
        String djPrice
) {
}
