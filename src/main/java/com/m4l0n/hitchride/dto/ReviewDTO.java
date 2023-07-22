package com.m4l0n.hitchride.dto;

// Programmer Name: Ang Ru Xian
// Program Name: ReviewDTO.java
// Description: This is a class that represents the review data transfer object
// Last Modified: 22 July 2023

public record ReviewDTO(
        String reviewId,
        String reviewDescription,
        Integer reviewRating,
        Long reviewTimestamp,
        RideDTO reviewRide
) {
}
