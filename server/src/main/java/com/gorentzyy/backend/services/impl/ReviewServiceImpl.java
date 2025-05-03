package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.CarNotFoundException;
import com.gorentzyy.backend.exceptions.ReviewNotFoundException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Review;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.ReviewRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.ReviewService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;


    @Autowired
    public ReviewServiceImpl(UserRepository userRepository, CarRepository carRepository, ModelMapper modelMapper, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.reviewRepository = reviewRepository;
    }
    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> createReview(ReviewDto reviewDto, String emailId, Long carId) {
        // Fetch the renter and booking
        User renter = userRepository.findByEmail(emailId)
                .orElseThrow(() -> new UserNotFoundException("User with email " + emailId + " not found"));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car with ID " + carId + " not found"));

        // Create and set up the review
        Review review = modelMapper.map(reviewDto, Review.class);
        review.setCreatedAt(LocalDateTime.now());
        review.setReviewer(renter);
        review.setCar(car);

        // Add the review to renter and booking
        renter.getReviews().add(review);
        car.getReviews().add(review);

        // Save the review
        Review savedReview = reviewRepository.save(review);
        carRepository.save(car);

        return new ResponseEntity<>(new ApiResponseObject(
                "The Review is saved successfully", true, modelMapper.map(savedReview, ReviewDto.class)
        ), HttpStatus.CREATED);
    }



    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updateReview(ReviewDto reviewDto, Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review with ID " + reviewId + " not found"));

        existingReview.setComments(reviewDto.getComments());
        existingReview.setRating(reviewDto.getRating());

        Review updatedReview = reviewRepository.save(existingReview);

        return new ResponseEntity<>(new ApiResponseObject(
                "Review updated successfully", true, modelMapper.map(updatedReview, ReviewDto.class)
        ), HttpStatus.OK);
    }


    // It should show review not found
    @Override
    public ResponseEntity<ApiResponseObject> getReview(Long reviewId) {
        try {
            // Fetch the review
            Review existingReview = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ReviewNotFoundException("Review with id " + reviewId + " not found"));

            // Log the successful fetching of review
            logger.info("Review with ID {} fetched successfully", reviewId);

            // Return the review as a DTO in the response
            return new ResponseEntity<>(new ApiResponseObject(
                    "Review fetched successfully", true, modelMapper.map(existingReview, ReviewDto.class)
            ), HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception and throw a custom exception for better error handling
            logger.error("Error fetching review with ID {}: {}", reviewId, e.getMessage());
            throw new ReviewNotFoundException("Failed to fetch the review due to an unexpected error.");
        }
    }

// No Content thing
@Override
public ResponseEntity<ApiResponseObject> deleteReview(Long reviewId) {
    Review existingReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review with ID " + reviewId + " not found"));

    reviewRepository.delete(existingReview);

    return new ResponseEntity<>(new ApiResponseObject("Review deleted successfully", true, null), HttpStatus.NO_CONTENT);
}



}
