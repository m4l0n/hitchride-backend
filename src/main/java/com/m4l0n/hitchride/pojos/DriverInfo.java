package com.m4l0n.hitchride.pojos;

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
