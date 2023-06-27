package com.m4l0n.hitchride.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverJourney {

    private String djId;
    private User djDriver;
    private Long djTimestamp;
    private OriginDestination djOriginDestination;
    private Integer djDestinationRange;
    private String djPrice;

}
