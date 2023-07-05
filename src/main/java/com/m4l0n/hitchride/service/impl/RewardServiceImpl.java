package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.pojos.Reward;
import com.m4l0n.hitchride.pojos.RewardCategory;
import com.m4l0n.hitchride.service.RewardService;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {

    private final CollectionReference rewardsRef;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public RewardServiceImpl(Firestore firestore, UserService userService, AuthenticationService authenticationService) {
        this.rewardsRef = firestore.collection("reward_category");
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Override
    public List<RewardCategory> getRewardsCategories() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshot = rewardsRef.get();
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            return document.getDocuments()
                    .stream()
                    .map(this::mapToRewardCategory)
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    public Reward createReward(Reward reward, String rcId) throws ExecutionException, InterruptedException {
        DocumentReference rewardCat = rewardsRef.document(rcId);

        ApiFuture<WriteResult> resultApiFuture = rewardCat.update("rcRewardsList", FieldValue.arrayUnion(reward));
        resultApiFuture.get();

        return reward;
    }

    @Override
    public Reward redeemReward(String rewardId) throws ExecutionException, InterruptedException {
        List<RewardCategory> rewardCategories = getRewardsCategories();
        for (RewardCategory rewardCategory : rewardCategories) {
            Optional<Reward> optionalReward = rewardCategory.getRcRewardsList()
                    .stream()
                    .filter(reward -> reward.getRewardId()
                            .equals(rewardId))
                    .findFirst();

            if (optionalReward.isPresent()) {
                Reward reward = optionalReward.get();
                if (reward.getRewardPointsRequired() > userService.getProfile()
                        .getUserPoints()) {
                    throw new HitchrideException("Not enough points to redeem this reward. ");
                }
                userService.updateUserPoints(authenticationService.getAuthenticatedUsername(), -reward.getRewardPointsRequired());
                return reward;
            }
        }
        return null;  // Return null if rewardId is not found or an exception is thrown
    }

    private RewardCategory mapToRewardCategory(DocumentSnapshot documentSnapshot) {
        RewardCategory rewardCategory = new RewardCategory();
        rewardCategory.setRcId(documentSnapshot.getId());
        rewardCategory.setRcName(documentSnapshot.getString("rcName"));
        rewardCategory.setRcRewardsList(mapToRewardList(documentSnapshot));

        return rewardCategory;
    }

    private List<Reward> mapToRewardList(DocumentSnapshot documentSnapshot) {
        List<Map<String, Object>> rewardsList = (List<Map<String, Object>>) documentSnapshot.get("rcRewardsList");

        if (rewardsList != null) {
            return rewardsList.stream()
                    .map(this::convertRewardMapToReward)
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    private Reward convertRewardMapToReward(Map<String, Object> rewardMap) {
        return new Reward(
                (String) rewardMap.get("rewardId"),
                (String) rewardMap.get("rewardTitle"),
                (String) rewardMap.get("rewardDescription"),
                Math.toIntExact((Long) rewardMap.get("rewardPointsRequired")),
                (String) rewardMap.get("rewardPhotoUrl"),
                Math.toIntExact((Long) rewardMap.get("rewardDuration"))
        );
    }

}
