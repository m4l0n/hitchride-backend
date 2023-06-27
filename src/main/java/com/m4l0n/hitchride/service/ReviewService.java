package com.m4l0n.hitchride.service;

import com.m4l0n.hitchride.dto.ReviewDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ReviewService {

    List<ReviewDTO> getUserReviews() throws ExecutionException, InterruptedException;

    ReviewDTO createReview(ReviewDTO review) throws ExecutionException, InterruptedException;

}
