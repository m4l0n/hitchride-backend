package com.m4l0n.hitchride.pojos;

import lombok.Data;

@Data
public class SearchRideCriteria {

    private LocationData searchRideLocationCriteria;
    private Long searchRideTimestampCriteria;

}
