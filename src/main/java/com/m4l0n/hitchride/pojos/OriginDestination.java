package com.m4l0n.hitchride.pojos;

import com.google.cloud.firestore.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginDestination {

    private GeoPoint origin;
    private GeoPoint destination;

}
