package com.m4l0n.hitchride.controller;

// Programmer's Name: Ang Ru Xian
// Program Name: RewardController.java
// Description: This is a class that consists of all controller methods related to rewards
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.Reward;
import com.m4l0n.hitchride.pojos.RewardCategory;
import com.m4l0n.hitchride.response.Response;
import com.m4l0n.hitchride.response.ResponseAPI;
import com.m4l0n.hitchride.service.RewardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(value = "/reward", produces = MediaType.APPLICATION_JSON_VALUE)
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/getRewardsCategories")
    public Response getRewardsCategories() {
        try {
            List<RewardCategory> rewardCategories = rewardService.getRewardsCategories();

            return ResponseAPI.positiveResponse(rewardCategories);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }

    @PostMapping("/createReward")
    public Response createReward(@RequestBody Reward reward, @RequestParam String rcId) {
        try {
            Reward newReward = rewardService.createReward(reward, rcId);

            return ResponseAPI.positiveResponse(reward);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }

    @GetMapping("/redeem")
    public Response redeemReward(@RequestParam String rewardId) {
        try {
            Reward reward = rewardService.redeemReward(rewardId);

            return ResponseAPI.positiveResponse(reward);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }
}
