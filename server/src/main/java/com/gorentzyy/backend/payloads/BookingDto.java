package com.gorentzyy.backend.payloads;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gorentzyy.backend.constants.AppConstants.Status;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class BookingDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long bookingId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @Positive(message = "Total price must be positive")
    private double totalPrice;

    @NotNull(message = "Booking status is required")
    private Status status;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    private CarDto car;
    private UserDto renter;


/*
  private Payment payment;

    private List<Review> reviews;

    private List<Promotion> promotions;

 */


}
