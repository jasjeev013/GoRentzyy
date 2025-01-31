package com.gorentzyy.backend.payloads;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gorentzyy.backend.config.AppConstants.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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


/*
 private Car car;
  private Payment payment;

    private User renter;
    private List<Review> reviews;

    private List<Promotion> promotions;

 */


}
