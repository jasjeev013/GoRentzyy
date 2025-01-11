package com.gorentzyy.backend.payloads;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {


    private Long reviewId;
    private int rating;
    private String comments;
    private LocalDateTime createdAt;
    /*
    private Booking booking;
    private User reviewer;

  */
}
