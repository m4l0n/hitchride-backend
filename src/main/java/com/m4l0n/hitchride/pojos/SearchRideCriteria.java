package com.m4l0n.hitchride.pojos;

import lombok.Data;

@Data
public class SearchRideCriteria {

    private OriginDestination searchRideLocationCriteria;
    private Long searchRideTimestampCriteria;

}
