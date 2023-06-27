package com.m4l0n.hitchride.dto;

import com.m4l0n.hitchride.pojos.LocationData;

public record OriginDestinationDTO(
        LocationData origin,
        LocationData destination
) {
}
