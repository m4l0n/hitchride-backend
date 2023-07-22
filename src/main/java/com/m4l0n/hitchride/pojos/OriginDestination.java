package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: OriginDestination.java
// Description: POJO that represents the origin and destination information
// Last Modified: 22 July 2023

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OriginDestination {

    private String origin;
    private String destination;

}
