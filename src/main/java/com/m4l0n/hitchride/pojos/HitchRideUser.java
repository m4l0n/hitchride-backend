package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: HitchRideUser.java
// Description: POJO that represents the user's information
// Last Modified: 22 July 2023

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
