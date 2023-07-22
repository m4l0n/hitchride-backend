package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: Review.java
// Description: POJO that represents the review information
// Last Modified: 22 July 2023

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    private String reviewId;
    private String reviewDescription;
    private Integer reviewRating;
    private Long reviewTimestamp;
    private String reviewRide;

}
