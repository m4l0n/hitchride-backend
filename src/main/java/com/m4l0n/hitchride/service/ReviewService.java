package com.m4l0n.hitchride.service;

// Programmer's Name: Ang Ru Xian
// Program Name: ReviewService.java
// Description: An interface that consists of methods to be implemented by the ReviewService class
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.dto.ReviewDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ReviewService {

    List<ReviewDTO> getUserReviews() throws ExecutionException, InterruptedException;

    ReviewDTO createReview(ReviewDTO review) throws ExecutionException, InterruptedException;

}
