package com.gorentzyy.backend.services.impl;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private ReviewServiceImpl reviewService;

    private ReviewDto reviewDto;
    private Review review;
    private User renter;
    private Booking booking;

    @BeforeEach
    void setUp() {
        reviewDto = new ReviewDto();
        reviewDto.setRating(4);
        reviewDto.setComments("Great experience");
        reviewDto.setCreatedAt(LocalDateTime.now());

        renter = new User();
        renter.setUserId(1L);
        renter.setEmail("test@example.com");
        renter.setReviews(new ArrayList<>());

        booking = new Booking();
        booking.setBookingId(1L);
//        booking.setReviews(new ArrayList<>());

        review = new Review();
        review.setReviewId(1L);
        review.setReviewer(renter);
//        review.setBooking(booking);
        review.setRating(4);
        review.setComments("Great experience");
        review.setCreatedAt(LocalDateTime.now());
    }

//    @Test
    void testCreateReview_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(renter));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(modelMapper.map(reviewDto, Review.class)).thenReturn(review);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(modelMapper.map(review, ReviewDto.class)).thenReturn(reviewDto);

        ResponseEntity<ApiResponseObject> response = reviewService.createReview(reviewDto, "test@example.com", 1L);

        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        Assertions.assertEquals("The Review is saved successfully", response.getBody().getMessage());
    }

    @Test
    void testCreateReview_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                reviewService.createReview(reviewDto, "test@example.com", 1L)
        );
    }

//    @Test
    void testUpdateReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(modelMapper.map(review, ReviewDto.class)).thenReturn(reviewDto);

        ResponseEntity<ApiResponseObject> response = reviewService.updateReview(reviewDto, 1L,review.getCar().getHost().getEmail());

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        assertEquals("Review updated successfully", response.getBody().getMessage());
    }

//    @Test
    void testUpdateReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () ->
                reviewService.updateReview(reviewDto, 1L,review.getCar().getHost().getEmail())
        );
    }

    @Test
    void testGetReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(modelMapper.map(review, ReviewDto.class)).thenReturn(reviewDto);

        ResponseEntity<ApiResponseObject> response = reviewService.getReview(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        assertEquals("Review fetched successfully", response.getBody().getMessage());
    }

    @Test
    void testGetReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () ->
                reviewService.getReview(1L)
        );
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);

        ResponseEntity<ApiResponseObject> response = reviewService.deleteReview(1L);

        assertEquals(204, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        assertEquals("Review deleted successfully", response.getBody().getMessage());
    }

    @Test
    void testDeleteReview_NotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () ->
                reviewService.deleteReview(1L)
        );
    }
}

