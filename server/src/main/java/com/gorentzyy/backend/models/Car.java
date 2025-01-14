package com.gorentzyy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    private String make;
    private String model;
    private int year;
    private String color;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private int seatingCapacity;
    private double rentalPricePerDay;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;

    private LocalDateTime maintenanceDueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @ManyToMany
    @JoinTable(
            name = "car_location",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private List<Location> locations;

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

