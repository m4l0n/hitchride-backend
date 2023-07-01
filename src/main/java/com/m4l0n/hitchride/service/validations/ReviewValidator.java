package com.m4l0n.hitchride.service.validations;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.m4l0n.hitchride.pojos.Review;

public class ReviewValidator {

    private final CollectionReference reviewRef;

    public ReviewValidator(CollectionReference reviewRef) {
        this.reviewRef = reviewRef;
    }

    public String validateCreateReview(Review review) {
        StringBuilder errors = new StringBuilder();

        this.validateReviewExists(errors, review.getReviewRide());

        return errors.toString();
    }

    private void validateReviewExists(StringBuilder errors, String reviewRideId) {
        try {
            ApiFuture<QuerySnapshot> documentSnapshot = reviewRef.whereEqualTo("reviewRide.rideId", reviewRideId).get();
            QuerySnapshot document = documentSnapshot.get();
            if (!document.isEmpty()) {
                errors.append("Review has already exist for this ride. ");
            }
        } catch (Exception e) {
            errors.append("Something went wrong while validating review. ");
        }
    }

}
