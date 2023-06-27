package com.m4l0n.hitchride.dto;

public record DriverJourneyDTO(
        String djId,
        UserDTO djDriver,
        Long djTimestamp,
        OriginDestinationDTO djOriginDestination,
        Integer djDestinationRange,
        String djPrice
) {
}
