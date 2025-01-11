package com.gorentzyy.backend.payloads;

import com.gorentzyy.backend.models.*;
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

    private Payment payment;
/*
 private Car car;


    private User renter;
    private List<Review> reviews;

    private List<Promotion> promotions;

 */

    public enum Status {
        CONFIRMED, CANCELED, MODIFIED
    }
}
