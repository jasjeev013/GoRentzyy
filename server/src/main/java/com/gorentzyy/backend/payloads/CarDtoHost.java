package com.gorentzyy.backend.payloads;

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
public class CarDtoHost implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

//    private UserDto host;

    private Long carId;
    @NotBlank(message = "Car name is required")
    private String name;

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
    private AppConstants.CarCategory carCategory;

    @NotNull(message = "Type is required")
    private AppConstants.CarType carType;

    @NotNull(message = "Fuel type is required")
    private AppConstants.FuelType fuelType;

    @NotNull(message = "Transmission Mode is required")
    private AppConstants.TransmissionMode transmissionMode;

    @Min(value = 1, message = "Seating capacity must be at least 1")
    private int seatingCapacity;

    @Min(value = 1, message = "Luggage capacity must be at least 1")
    private int luggageCapacity;;

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


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    private String insurance;
    private Boolean roadSideAssistance;
    private String fuelPolicy;
    private String features;
    private String importantPoints;

    private LocationDto location;
    private List<ReviewDto> reviews;


    /*
    private User host;
    private List<Booking> bookings;
     */


}

