package com.m4l0n.hitchride.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardCategory {

    private String rcId;
    private String rcName;
    private List<Reward> rcRewardsList;

}
