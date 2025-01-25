package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.BookingNotFoundException;
import com.gorentzyy.backend.exceptions.ReviewNotFoundException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Review;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ReviewDto;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.ReviewRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl implements ReviewService {

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

    @Override
    public ResponseEntity<ApiResponseObject> createReview(ReviewDto reviewDto, Long renterId, Long bookingId) {
        User renter = userRepository.findById(renterId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + renterId + " not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        Review review = modelMapper.map(reviewDto, Review.class);

        LocalDateTime now = LocalDateTime.now();
        review.setCreatedAt(now);
        review.setReviewer(renter);
        renter.getReviews().add(review);
        review.setBooking(booking);
        booking.getReviews().add(review);

        bookingRepository.save(booking);
        userRepository.save(renter);

        Review savedReview = reviewRepository.save(review);
        return new ResponseEntity<>(new ApiResponseObject("The Review is saved successfully", true, modelMapper.map(savedReview, ReviewDto.class)), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<ApiResponseObject> updateReview(ReviewDto reviewDto, Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + reviewId + " not found"));

        existingReview.setComments(reviewDto.getComments());
        existingReview.setRating(reviewDto.getRating());

        Review updatedReview = reviewRepository.save(existingReview);
        return new ResponseEntity<>(new ApiResponseObject("Review updated successfully", true, modelMapper.map(updatedReview, ReviewDto.class)), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<ApiResponseObject> getReview(Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + reviewId + " not found"));

        return new ResponseEntity<>(new ApiResponseObject("Review fetched successfully", true, modelMapper.map(existingReview, ReviewDto.class)), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<ApiResponseObject> deleteReview(Long reviewId) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + reviewId + " not found"));

        reviewRepository.delete(existingReview);
        return new ResponseEntity<>(new ApiResponseObject("Review deleted successfully", true, null), HttpStatus.OK);
    }

}
