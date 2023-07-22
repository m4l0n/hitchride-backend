package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: Reward.java
// Description: POJO that represents the reward information
// Last Modified: 22 July 2023

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reward {

    private String rewardId;
    private String rewardTitle;
    private String rewardDescription;
    private Integer rewardPointsRequired;
    private String rewardPhotoUrl;
    private Integer rewardDuration;

}
