package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: LocationData.java
// Description: POJO that represents the location information
// Last Modified: 22 July 2023

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationData {

    private String placeId;
    private String addressName;
    private String addressString;

}
