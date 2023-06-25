package com.m4l0n.hitchride.pojos;

import com.google.cloud.firestore.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    private String rideId;
    private User ridePassenger;
    private GeoPoint rideOrigin;
    private GeoPoint rideDestination;
    private DriverJourney rideDriverJourney;

}
