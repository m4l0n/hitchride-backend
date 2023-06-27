package com.m4l0n.hitchride.dto;

public record SearchRideCriteriaDTO(
        OriginDestinationDTO searchRideLocationCriteria,
        Long searchRideTimestampCriteria
) {
}
