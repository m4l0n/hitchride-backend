package com.m4l0n.hitchride.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchRideCriteria {

    private OriginDestination searchRideLocationCriteria;
    private Long searchRideTimestampCriteria;

}
