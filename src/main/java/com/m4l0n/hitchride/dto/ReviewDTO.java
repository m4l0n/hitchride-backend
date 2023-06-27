package com.m4l0n.hitchride.dto;

public record ReviewDTO(
        String reviewId,
        String reviewDescription,
        Integer reviewRating,
        Long reviewTimestamp,
        RideDTO reviewRide
) {
}
