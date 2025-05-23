package com.gorentzyy.backend.models;

import com.gorentzyy.backend.constants.AppConstants;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    private String name;

    private String make;
    private String model;
    private int year;
    private String color;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @ElementCollection
    private List<String> photos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AppConstants.CarCategory carCategory;

    @Enumerated(EnumType.STRING)
    private AppConstants.CarType carType;

    @Enumerated(EnumType.STRING)
    private AppConstants.FuelType fuelType;

    @Enumerated(EnumType.STRING)
    private AppConstants.TransmissionMode transmissionMode;

    private int seatingCapacity;
    private int luggageCapacity;

    // Base rental prices
    private double rentalPricePerDay;
    private double rentalPricePerWeek;
    private double rentalPricePerMonth;

    @Enumerated(EnumType.STRING)
    private AppConstants.AvailabilityStatus availabilityStatus;

    private LocalDateTime maintenanceDueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String insurance;
    private Boolean roadSideAssistance;
    private String fuelPolicy;
    private String features;
    private String importantPoints;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Booking> bookings;


    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @ManyToMany(mappedBy = "cars")
    private List<Promotion> promotions;

    @OneToOne
    @JoinTable(
            name = "car_location",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Location location;


}


