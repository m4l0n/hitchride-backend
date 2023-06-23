package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.pojos.Reward;
import com.m4l0n.hitchride.pojos.RewardCategory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface RewardService {

    List<RewardCategory> getRewardsCategories() throws ExecutionException, InterruptedException;

    Reward createReward(Reward reward, String rcId) throws ExecutionException, InterruptedException;

}
