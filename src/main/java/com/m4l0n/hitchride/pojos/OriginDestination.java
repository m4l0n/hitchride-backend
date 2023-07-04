package com.m4l0n.hitchride.pojos;

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
