package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: Ride.java
// Description: POJO that represents the ride information
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    private String rideId;
    private String ridePassenger;
    private OriginDestination rideOriginDestination;
    private String rideDriverJourney;
    private RideStatus rideStatus;

    public Ride(String rideId, String ridePassenger, OriginDestination rideOriginDestination, String rideDriverJourney) {
        this.rideId = rideId;
        this.ridePassenger = ridePassenger;
        this.rideOriginDestination = rideOriginDestination;
        this.rideDriverJourney = rideDriverJourney;
    }

}
