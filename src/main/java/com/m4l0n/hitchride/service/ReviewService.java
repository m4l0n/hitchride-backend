package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.pojos.Review;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ReviewService {

    List<Review> getUserReviews() throws ExecutionException, InterruptedException;

    Review createReview(Review review) throws ExecutionException, InterruptedException;

}
