package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.ReviewDTO;
import com.m4l0n.hitchride.pojos.Review;
import com.m4l0n.hitchride.service.RideService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper implements BaseMapper<Review, ReviewDTO> {

    private final RideService rideService;

    public ReviewMapper(RideService rideService) {
        this.rideService = rideService;
    }

    @SneakyThrows
    @Override
    public ReviewDTO mapPojoToDto(Review pojo) {
       return new ReviewDTO(
               pojo.getReviewId(),
               pojo.getReviewDescription(),
               pojo.getReviewRating(),
               pojo.getReviewTimestamp(),
               rideService.getRideById(pojo.getReviewRide())
       );
    }

    @Override
    public Review mapDtoToPojo(ReviewDTO dto) {
        return new Review(
                dto.reviewId(),
                dto.reviewDescription(),
                dto.reviewRating(),
                dto.reviewTimestamp(),
                dto.reviewRide().rideId()
        );
    }

}

