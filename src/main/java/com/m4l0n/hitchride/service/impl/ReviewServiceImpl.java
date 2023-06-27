package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.m4l0n.hitchride.dto.ReviewDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.ReviewMapper;
import com.m4l0n.hitchride.pojos.Review;
import com.m4l0n.hitchride.service.ReviewService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.ReviewValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final CollectionReference reviewRef;
    private final ReviewValidator reviewValidator;
    private final AuthenticationService authenticationService;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(Firestore firestore, AuthenticationService authenticationService, ReviewMapper reviewMapper) {
        this.reviewRef = firestore.collection("reviews");
        this.authenticationService = authenticationService;
        this.reviewMapper = reviewMapper;
        this.reviewValidator = new ReviewValidator(reviewRef);
    }

    @Override
    public List<ReviewDTO> getUserReviews() throws ExecutionException, InterruptedException {
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        List<ReviewDTO> driverReviews;
        //Get reviews where user is driver
        ApiFuture<QuerySnapshot> querySnapshot = reviewRef.whereEqualTo("reviewRide.rideDriverJourney.djDriver.userId", currentLoggedInUser)
                .get();
        QuerySnapshot document = querySnapshot.get();

        if (!document.isEmpty()) {
            driverReviews = document.toObjects(Review.class)
                    .stream()
                    .map(reviewMapper::mapPojoToDto)
                    .toList();
        } else {
            driverReviews = List.of();
        }
        //Get reviews where user is passenger
        querySnapshot = reviewRef.whereEqualTo("reviewRide.ridePassenger.userId", currentLoggedInUser)
                .get();
        document = querySnapshot.get();

        List<ReviewDTO> passengerReviews = document.toObjects(Review.class)
                .stream()
                .map(reviewMapper::mapPojoToDto)
                .toList();

        if (!document.isEmpty()) {
            return Stream.concat(driverReviews.stream(), passengerReviews.stream())
                    .toList();
        }
        return driverReviews;
    }

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) throws ExecutionException, InterruptedException, HitchrideException {
        Review review = reviewMapper.mapDtoToPojo(reviewDTO);
        String currentLoggedInUser = authenticationService.getAuthenticatedUsername();
        String errors = reviewValidator.validateCreateReview(review, currentLoggedInUser);
        if (!errors.isEmpty()) {
            throw new HitchrideException(errors);
        }
        String docId = reviewRef.document()
                .getId();
        review.setReviewId(docId);
        WriteResult writeResult = reviewRef.document(docId)
                .set(review)
                .get();
        return reviewDTO;
    }
}
