package com.gorentzyy.backend.payloads;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long reviewId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private int rating;

    @Size(max = 500, message = "Comments cannot exceed 500 characters")
    private String comments;

    @PastOrPresent(message = "Review date must be in the past or present")
    private LocalDateTime createdAt;

/*
    private Booking booking;
    private User reviewer;
  */
}
