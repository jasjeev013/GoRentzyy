package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import com.gorentzyy.backend.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @PreAuthorize("hasRole('ROLE_RENTER')")
    @PostMapping("/create/{carId}")
    public ResponseEntity<ApiResponseObject> createReview(@Valid @RequestBody ReviewDto reviewDto, Authentication authentication, @PathVariable Long carId){
        String email = authentication.getName();
        return reviewService.createReview(reviewDto,email,carId);
    }

    @PreAuthorize("hasRole('RENTER')")
    @PutMapping("/update/{reviewId}")
    public ResponseEntity<ApiResponseObject> updateReview(@Valid @RequestBody ReviewDto reviewDto, @PathVariable Long reviewId){
        return reviewService.updateReview(reviewDto,reviewId);
    }

    @GetMapping("/get/{reviewId}")
    public ResponseEntity<ApiResponseObject> getReview(@PathVariable Long reviewId){
        return reviewService.getReview(reviewId);
    }

    @PreAuthorize("hasRole('RENTER')")
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<ApiResponseObject> deleteReview(@PathVariable Long reviewId){
        return reviewService.deleteReview(reviewId);
    }
}
