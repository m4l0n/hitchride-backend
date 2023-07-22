package com.m4l0n.hitchride.mapping;

// Programmer's Name: Ang Ru Xian
// Program Name: ReviewMapper.java
// Description: Mapper for Review DTO and POJO
// Last Modified: 22 July 2023

import com.m4l0n.hitchride.dto.ReviewDTO;
import com.m4l0n.hitchride.pojos.Review;
import com.m4l0n.hitchride.service.RideService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper implements BaseMapper<Review, ReviewDTO> {

    private final RideService rideService;
    private final RideMapper rideMapper;

    public ReviewMapper(RideService rideService, RideMapper rideMapper) {
        this.rideService = rideService;
        this.rideMapper = rideMapper;
    }

    @SneakyThrows
    @Override
    public ReviewDTO mapPojoToDto(Review pojo) {
       return new ReviewDTO(
               pojo.getReviewId(),
               pojo.getReviewDescription(),
               pojo.getReviewRating(),
               pojo.getReviewTimestamp(),
                rideMapper.mapPojoToDto(rideService.getRideById(pojo.getReviewRide()))
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

