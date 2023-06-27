package com.m4l0n.hitchride.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitchRideUser {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userPhotoUrl;
    private Integer userPoints;
    private DriverInfo userDriverInfo;

}
