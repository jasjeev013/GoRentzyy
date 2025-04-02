package com.gorentzyy.backend.payloads;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gorentzyy.backend.constants.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long carId;

    @NotBlank(message = "Car make is required")
    private String make;

    @NotBlank(message = "Car model is required")
    private String model;

    @Min(value = 1886, message = "Year must be realistic (greater than 1886)")
    @Max(value = 2050, message = "Year must be reasonable")
    private int year;

    @NotBlank(message = "Car color is required")
    private String color;

    @NotBlank(message = "Registration number is required")
    @Size(min = 5, max = 15, message = "Registration number must be between 5 and 15 characters")
    private String registrationNumber;

    private List<String> photos = new ArrayList<>();

    @NotNull(message = "Category is required")
    private AppConstants.Category category;

    @NotNull(message = "Fuel type is required")
    private AppConstants.FuelType fuelType;

    @Min(value = 1, message = "Seating capacity must be at least 1")
    private int seatingCapacity;

    @Positive(message = "Rental price per day must be positive")
    private double rentalPricePerDay;

    @Positive(message = "Rental price per week must be positive")
    private double rentalPricePerWeek;

    @Positive(message = "Rental price per month must be positive")
    private double rentalPricePerMonth;

    @NotNull(message = "Availability status is required")
    private AppConstants.AvailabilityStatus availabilityStatus;

    @FutureOrPresent(message = "Maintenance due date must be in the present or future")
    private LocalDateTime maintenanceDueDate;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    /*
    private User host;
    private List<Booking> bookings;
    private List<Location> locations;
     */


}
