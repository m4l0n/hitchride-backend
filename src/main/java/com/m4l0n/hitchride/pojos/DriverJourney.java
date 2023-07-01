package com.m4l0n.hitchride.pojos;

import com.m4l0n.hitchride.enums.DJStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverJourney {

    private String djId;
    private String djDriver;
    private Long djTimestamp;
    private OriginDestination djOriginDestination;
    private Integer djDestinationRange;
    private String djPrice;
    private DJStatus djStatus;

    public DriverJourney(String djId, String djDriver, Long djTimestamp, OriginDestination djOriginDestination, Integer djDestinationRange, String djPrice) {
        this.djId = djId;
        this.djDriver = djDriver;
        this.djTimestamp = djTimestamp;
        this.djOriginDestination = djOriginDestination;
        this.djDestinationRange = djDestinationRange;
        this.djPrice = djPrice;
    }

}
