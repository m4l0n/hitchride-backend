package com.m4l0n.hitchride.pojos;

import com.google.cloud.firestore.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverJourney {

    private String djId;
    private User djDriver;
    private Long djTimestamp;
    private GeoPoint djOrigin;
    private GeoPoint djDestination;
    private Integer djDestinationRange;

}
