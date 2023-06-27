package com.m4l0n.hitchride.mapping;

import com.m4l0n.hitchride.dto.ReviewDTO;
import com.m4l0n.hitchride.pojos.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper implements BaseMapper<Review, ReviewDTO> {

    private final RideMapper rideMapper;

    public ReviewMapper(RideMapper rideMapper) {
        this.rideMapper = rideMapper;
    }

    @Override
    public ReviewDTO mapPojoToDto(Review pojo) {
       return new ReviewDTO(
               pojo.getReviewId(),
               pojo.getReviewDescription(),
               pojo.getReviewRating(),
               pojo.getReviewTimestamp(),
               rideMapper.mapPojoToDto(pojo.getReviewRide())
       );
    }

    @Override
    public Review mapDtoToPojo(ReviewDTO dto) {
        return new Review(
                dto.reviewId(),
                dto.reviewDescription(),
                dto.reviewRating(),
                dto.reviewTimestamp(),
                rideMapper.mapDtoToPojo(dto.reviewRide())
        );
    }

}

