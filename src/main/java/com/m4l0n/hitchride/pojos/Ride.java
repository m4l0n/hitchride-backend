package com.m4l0n.hitchride.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    private String rideId;
    private User ridePassenger;
    private OriginDestination rideOriginDestination;
    private DriverJourney rideDriverJourney;

}
