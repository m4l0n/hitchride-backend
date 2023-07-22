package com.m4l0n.hitchride.dto;

// Programmer Name: Ang Ru Xian
// Program Name: OriginDestinationDTO.java
// Description: This is a class that represents the origin destination data transfer object
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.pojos.LocationData;

public record OriginDestinationDTO(
        LocationData origin,
        LocationData destination
) {
}
