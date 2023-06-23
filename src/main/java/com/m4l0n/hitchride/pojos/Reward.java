package com.m4l0n.hitchride.pojos;

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
