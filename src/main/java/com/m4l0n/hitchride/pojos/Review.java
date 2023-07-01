package com.m4l0n.hitchride.pojos;

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
