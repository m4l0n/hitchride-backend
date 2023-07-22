package com.m4l0n.hitchride.pojos;

// Programmer's Name: Ang Ru Xian
// Program Name: RewardCategory.java
// Description: POJO that represents the reward category information
// Last Modified: 22 July 2023

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
