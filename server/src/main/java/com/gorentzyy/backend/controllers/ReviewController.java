package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import com.gorentzyy.backend.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create/{renterId}/{bookingId}")
    public ResponseEntity<ApiResponseObject> createReview(@Valid @RequestBody ReviewDto reviewDto, @PathVariable Long renterId, @PathVariable Long bookingId){
        return reviewService.createReview(reviewDto,renterId,bookingId);
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<ApiResponseObject> updateReview(@Valid @RequestBody ReviewDto reviewDto,@PathVariable Long reviewId){
        return reviewService.updateReview(reviewDto,reviewId);
    }

    @GetMapping("/get/{reviewId}")
    public ResponseEntity<ApiResponseObject> getReview(@PathVariable Long reviewId){
        return reviewService.getReview(reviewId);
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<ApiResponseObject> deleteReview(@PathVariable Long reviewId){
        return reviewService.deleteReview(reviewId);
    }
}
