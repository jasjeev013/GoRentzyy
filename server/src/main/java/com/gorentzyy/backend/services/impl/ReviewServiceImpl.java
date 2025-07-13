package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Review;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.ReviewRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.ReviewService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(UserRepository userRepository,
                             CarRepository carRepository,
                             ModelMapper modelMapper,
                             ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> createReview(ReviewDto reviewDto, String emailId, Long carId) {
        logger.info("Creating review for car ID: {} by user: {}", carId, emailId);

        try {
            validateReviewDto(reviewDto);
            User renter = getUserByEmail(emailId);
            Car car = getCarById(carId);

            // Check if user has already reviewed this car
            if (reviewRepository.existsByReviewerAndCar(renter, car)) {
                logger.warn("User {} already reviewed car {}", emailId, carId);
                throw new DuplicateReviewException("You have already reviewed this car");
            }

            Review review = createReviewFromDto(reviewDto, renter, car);
            Review savedReview = reviewRepository.save(review);


            logger.info("Successfully created review ID: {} for car ID: {}",
                    savedReview.getReviewId(), carId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseObject(
                            "Review created successfully",
                            true,
                            modelMapper.map(savedReview, ReviewDto.class))
                    );

        } catch (UserNotFoundException | CarNotFoundException |
                 InvalidReviewException | DuplicateReviewException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating review for car ID: {} by user: {}", carId, emailId, e);
            throw new ReviewProcessingException("Failed to create review");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updateReview(ReviewDto reviewDto, Long reviewId, String email) {
        logger.info("Updating review ID: {} by user: {}", reviewId, email);

        try {
            validateReviewDto(reviewDto);
            Review review = getReviewById(reviewId);
            validateReviewOwnership(review, email);

            updateReviewFromDto(review, reviewDto);
            Review updatedReview = reviewRepository.save(review);



            logger.info("Successfully updated review ID: {}", reviewId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Review updated successfully",
                    true,
                    modelMapper.map(updatedReview, ReviewDto.class))
            );

        } catch (ReviewNotFoundException | InvalidReviewException |
                 UnauthorizedAccessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating review ID: {}", reviewId, e);
            throw new ReviewProcessingException("Failed to update review");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getReview(Long reviewId) {
        logger.info("Fetching review ID: {}", reviewId);

        try {
            Review review = getReviewById(reviewId);

            logger.info("Successfully fetched review ID: {}", reviewId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Review retrieved successfully",
                    true,
                    modelMapper.map(review, ReviewDto.class))
            );

        } catch (ReviewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching review ID: {}", reviewId, e);
            throw new ReviewProcessingException("Failed to retrieve review");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> deleteReview(Long reviewId) {
        logger.info("Deleting review ID: {}", reviewId);

        try {
            Review review = getReviewById(reviewId);
            reviewRepository.delete(review);


            logger.info("Successfully deleted review ID: {}", reviewId);

            return ResponseEntity.ok()
                    .body(new ApiResponseObject(
                            "Review deleted successfully",
                            true,
                            null)
                    );

        } catch (ReviewNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting review ID: {}", reviewId, e);
            throw new ReviewProcessingException("Failed to delete review");
        }
    }

    // Helper methods
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UserNotFoundException("User not found");
                });
    }

    private Car getCarById(Long carId) {
        return carRepository.findById(carId)
                .orElseThrow(() -> {
                    logger.error("Car not found with ID: {}", carId);
                    return new CarNotFoundException("Car not found");
                });
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    logger.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException("Review not found");
                });
    }

    private void validateReviewDto(ReviewDto reviewDto) {
        if (reviewDto == null) {
            throw new InvalidReviewException("Review data cannot be null");
        }
        if (!StringUtils.hasText(reviewDto.getComments())) {
            throw new InvalidReviewException("Review comments cannot be empty");
        }
        if (reviewDto.getRating() < MIN_RATING || reviewDto.getRating() > MAX_RATING) {
            throw new InvalidReviewException(String.format("Rating must be between %d and %d", MIN_RATING, MAX_RATING));
        }
    }

    private void validateReviewOwnership(Review review, String email) {
        if (!Objects.equals(review.getReviewer().getEmail(), email) &&
                !Objects.equals(review.getCar().getHost().getEmail(), email)) {
            logger.warn("Unauthorized review update attempt by: {}", email);
            throw new UnauthorizedAccessException("You are not authorized to modify this review");
        }
    }

    private Review createReviewFromDto(ReviewDto reviewDto, User reviewer, Car car) {
        Review review = modelMapper.map(reviewDto, Review.class);
        review.setReviewer(reviewer);
        review.setCar(car);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    private void updateReviewFromDto(Review review, ReviewDto reviewDto) {
        review.setComments(reviewDto.getComments());
        review.setRating(reviewDto.getRating());
    }


}