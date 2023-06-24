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

    public String userId;
    public String userName;
    public String userEmail;
    public String userPhoneNumber;
    public String userPhotoUrl;
    public Integer userPoints;
    public Map<String, GeoPoint> userSavedLocations;
    public DriverInfo userDriverInfo;

}
