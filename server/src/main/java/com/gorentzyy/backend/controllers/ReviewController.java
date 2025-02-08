package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import com.gorentzyy.backend.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/review")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create/{bookingId}")
    public ResponseEntity<ApiResponseObject> createReview(@Valid @RequestBody ReviewDto reviewDto, Authentication authentication, @PathVariable Long bookingId){
        String email = authentication.getName();
        return reviewService.createReview(reviewDto,email,bookingId);
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
