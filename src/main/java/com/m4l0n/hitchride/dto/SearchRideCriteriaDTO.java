package com.m4l0n.hitchride.dto;

// Programmer Name: Ang Ru Xian
// Program Name: SearchRideCriteriaDTO.java
// Description: This is a class that represents the search ride criteria data transfer object
// Last Modified: 22 July 2023

public record SearchRideCriteriaDTO(
        OriginDestinationDTO searchRideLocationCriteria,
        Long searchRideTimestampCriteria
) {
}
