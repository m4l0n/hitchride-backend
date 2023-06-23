package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.m4l0n.hitchride.pojos.Reward;
import com.m4l0n.hitchride.pojos.RewardCategory;
import com.m4l0n.hitchride.service.RewardService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {

    private final CollectionReference rewardsRef;

    public RewardServiceImpl(Firestore firestore) {
        this.rewardsRef = firestore.collection("reward_category");
    }

    @Override
    public List<RewardCategory> getRewardsCategories() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> querySnapshot = rewardsRef.get();
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            return document.getDocuments().stream()
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
