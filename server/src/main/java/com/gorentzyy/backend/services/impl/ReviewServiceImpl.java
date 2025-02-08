package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Review;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import com.gorentzyy.backend.repositories.BookingRepository;
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
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;


    @Autowired
    public ReviewServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, ModelMapper modelMapper, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
        this.reviewRepository = reviewRepository;
    }
    // the renter can review diff. renter's booking
    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> createReview(ReviewDto reviewDto, String emailId, Long bookingId) {
        try {
            // Fetch the renter and booking
            User renter = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> new UserNotFoundException("User with id " + emailId + " not found"));
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

            // Create and set up the review
            Review review = modelMapper.map(reviewDto, Review.class);
            LocalDateTime now = LocalDateTime.now();
            review.setCreatedAt(now);
            review.setReviewer(renter);
            review.setBooking(booking);

            // Add the review to renter and booking
            renter.getReviews().add(review);
            booking.getReviews().add(review);

            // Save the changes (Booking and User are saved automatically due to cascading or explicit save)
            Review savedReview = reviewRepository.save(review);

            // Log successful creation of the review
            logger.info("Review created successfully for Booking ID: {}", bookingId);

            // Return response with the saved review
            return new ResponseEntity<>(new ApiResponseObject(
                    "The Review is saved successfully", true, modelMapper.map(savedReview, ReviewDto.class)
            ), HttpStatus.CREATED);

        } catch (Exception e) {
            // Handle any unexpected errors (e.g., database issues)
            logger.error("Error creating review for Booking ID {}: {}", bookingId, e.getMessage());
            throw new ReviewCreationException("Failed to create the review due to an unexpected error.");
        }
    }


    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updateReview(ReviewDto reviewDto, Long reviewId) {
        try {
            // Find the existing review
            Review existingReview = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ReviewNotFoundException("Review with id " + reviewId + " not found"));

            // Update the review fields
            existingReview.setComments(reviewDto.getComments());
            existingReview.setRating(reviewDto.getRating());

            // Save the updated review
            Review updatedReview = reviewRepository.save(existingReview);

            // Log the successful review update
            logger.info("Review with ID {} updated successfully", reviewId);

            // Return response with the updated review
            return new ResponseEntity<>(new ApiResponseObject(
                    "Review updated successfully", true, modelMapper.map(updatedReview, ReviewDto.class)
            ), HttpStatus.OK);
        } catch (Exception e) {
            // Log any unexpected error and throw a custom exception
            logger.error("Error updating review with ID {}: {}", reviewId, e.getMessage());
            throw new ReviewUpdateException("Failed to update the review due to an unexpected error.");
        }
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
        try {
            // Fetch the review to be deleted
            Review existingReview = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ReviewNotFoundException("Review with id " + reviewId + " not found"));

            // Log the deletion
            logger.info("Review with ID {} is being deleted", reviewId);

            // Delete the review
            reviewRepository.delete(existingReview);

            // Return a response with no content
            return new ResponseEntity<>(new ApiResponseObject("Review deleted successfully", true, null), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Log the exception and throw a custom exception
            logger.error("Error deleting review with ID {}: {}", reviewId, e.getMessage());
            throw new DatabaseException("Failed to delete the review due to an unexpected error.");
        }
    }


}
