package com.m4l0n.hitchride.controller;


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
import java.util.Map;

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

    @PostMapping("/redeem")
    public Response redeemReward(@RequestBody Map<String, String> rewardId) {
        try {
            if (!rewardId.containsKey("rewardId")) {
                throw new HitchrideException("Reward id is required");
            }
            Reward reward = rewardService.redeemReward(rewardId.get("rewardId"));

            return ResponseAPI.positiveResponse(reward);
        } catch (Exception e) {
            throw new HitchrideException(e.getMessage());
        }
    }
}
