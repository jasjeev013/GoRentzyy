package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<ApiResponseObject> createReview(ReviewDto reviewDto,String email,Long carId);
    ResponseEntity<ApiResponseObject> updateReview(ReviewDto reviewDto,Long reviewId,String email);
    ResponseEntity<ApiResponseObject> getReview(Long reviewId);
    ResponseEntity<ApiResponseObject> deleteReview(Long reviewId);

}
