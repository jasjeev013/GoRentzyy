package com.gorentzyy.backend.payloads;


import com.gorentzyy.backend.config.AppConstants;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {
    private Long carId;


    private String make;
    private String model;
    private int year;
    private String color;
    private String registrationNumber;


    private AppConstants.Category category;


    private AppConstants.FuelType fuelType;

    private int seatingCapacity;
    private double rentalPricePerDay;
    private double rentalPricePerWeek;
    private double rentalPricePerMonth;


    private AppConstants.AvailabilityStatus availabilityStatus;

    private LocalDateTime maintenanceDueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*
    private User host;
    private List<Booking> bookings;
    private List<Location> locations;
     */


}
