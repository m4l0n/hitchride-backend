package com.m4l0n.hitchride.pojos;

import com.google.cloud.firestore.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userPhotoUrl;
    private Integer userPoints;
    private Map<String, GeoPoint> userSavedLocations;
    private DriverInfo userDriverInfo;

}
