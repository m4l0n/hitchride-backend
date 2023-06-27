package com.m4l0n.hitchride.dto;

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
