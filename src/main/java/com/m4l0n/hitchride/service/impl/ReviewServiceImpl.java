package com.m4l0n.hitchride.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.m4l0n.hitchride.dto.ReviewDTO;
import com.m4l0n.hitchride.exceptions.HitchrideException;
import com.m4l0n.hitchride.mapping.ReviewMapper;
import com.m4l0n.hitchride.pojos.Review;
import com.m4l0n.hitchride.service.ReviewService;
import com.m4l0n.hitchride.service.RideService;
import com.m4l0n.hitchride.service.UserService;
import com.m4l0n.hitchride.service.shared.AuthenticationService;
import com.m4l0n.hitchride.service.validations.ReviewValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final CollectionReference reviewRef;
    private final ReviewValidator reviewValidator;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;
    private final RideService rideService;

    public ReviewServiceImpl(Firestore firestore, AuthenticationService authenticationService, UserService userService, ReviewMapper reviewMapper, RideService rideService) {
        this.reviewRef = firestore.collection("reviews");
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.reviewMapper = reviewMapper;
        this.rideService = rideService;
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
            driverReviews = document.getDocuments()
                    .stream()
                    .map(this::mapDocumentToPojo)
                    .map(reviewMapper::mapPojoToDto)
                    .toList();
        } else {
            driverReviews = List.of();
        }
        //Get reviews where user is passenger
        querySnapshot = reviewRef.whereEqualTo("reviewRide.ridePassenger.userId", currentLoggedInUser)
                .get();
        document = querySnapshot.get();

        List<ReviewDTO> passengerReviews = document.getDocuments()
                .stream()
                .map(this::mapDocumentToPojo)
                .map(reviewMapper::mapPojoToDto)
                .toList();

        if (!document.isEmpty()) {
            return Stream.concat(driverReviews.stream(), passengerReviews.stream())
                    .toList();
        }
        return driverReviews;
    }

    @Override
    public ReviewDTO createReview(ReviewDTO reviewDTO) throws ExecutionException, InterruptedException {
        Review review = reviewMapper.mapDtoToPojo(reviewDTO);

        DocumentReference documentReference = rideService.getRideReferenceById(review.getReviewRide());

        QuerySnapshot querySnapshot = reviewRef.whereEqualTo("reviewRide", documentReference)
                .get()
                .get();
        if (!querySnapshot.isEmpty()) {
            throw new HitchrideException("Review already exists for this ride");
        }

        String docId = reviewRef.document()
                .getId();
        review.setReviewId(docId);

        reviewRef.document(docId)
                .set(review)
                .get();
        reviewRef.document(docId).update("reviewRide", documentReference).get();

        userService.updateDriverRatings(review.getReviewRating());
        return reviewDTO;
    }

    private Review mapDocumentToPojo(DocumentSnapshot document) {
        Map<String, Object> objectMap = document.getData();
        return new Review(
                (String) objectMap.get("reviewId"),
                (String) objectMap.get("reviewDescription"),
                ((Number) objectMap.get("reviewRating")).intValue(),
                ((Number) objectMap.get("reviewTimestamp")).longValue(),
                ((DocumentSnapshot) objectMap.get("reviewRide")).getId()
        );
    }
}
