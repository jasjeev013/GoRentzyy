package com.gorentzyy.backend.payloads;


import com.gorentzyy.backend.config.AppConstants.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long bookingId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double totalPrice;

    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


/*
 private Car car;
  private Payment payment;

    private User renter;
    private List<Review> reviews;

    private List<Promotion> promotions;

 */


}
