package com.gorentzyy.backend.payloads;


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


    private Category category;


    private FuelType fuelType;

    private int seatingCapacity;
    private double rentalPricePerDay;
    private double rentalPricePerWeek;
    private double rentalPricePerMonth;


    private AvailabilityStatus availabilityStatus;

    private LocalDateTime maintenanceDueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*
    private User host;
    private List<Booking> bookings;
    private List<Location> locations;
     */

    public enum Category {
        ECONOMY, LUXURY, SUV, OTHER
    }

    public enum FuelType {
        PETROL, DIESEL, ELECTRIC,CNG, OTHER
    }

    public enum AvailabilityStatus {
        AVAILABLE, RESERVED, UNDER_MAINTENANCE
    }
}
