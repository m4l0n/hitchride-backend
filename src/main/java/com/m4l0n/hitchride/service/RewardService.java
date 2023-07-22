package com.m4l0n.hitchride.service;

// Programmer's Name: Ang Ru Xian
// Program Name: RewardService.java
// Description: An interface that consists of methods to be implemented by the RewardService class
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.pojos.Reward;
import com.m4l0n.hitchride.pojos.RewardCategory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface RewardService {

    List<RewardCategory> getRewardsCategories() throws ExecutionException, InterruptedException;

    Reward createReward(Reward reward, String rcId) throws ExecutionException, InterruptedException;

    Reward redeemReward(String rewardId) throws ExecutionException, InterruptedException;

}
