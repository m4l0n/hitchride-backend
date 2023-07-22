package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: DriverInfo.java
// Description: POJO that represents the user's driver information
// Last Modified: 22 July 2023

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverInfo {

    private String diCarBrand;
    private String diCarModel;
    private String diCarColor;
    private String diCarLicensePlate;
    private Long diDateJoinedTimestamp;
    private Long diDateCarBoughtTimestamp;
    private Boolean diIsCarSecondHand;
    private Integer diRating;
    private Integer diNumberOfRatings;

}
