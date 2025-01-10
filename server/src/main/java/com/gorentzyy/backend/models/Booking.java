package com.gorentzyy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "renter_id")
    private User renter;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @ManyToMany(mappedBy = "bookings")
    private List<Promotion> promotions;

    public enum Status {
        CONFIRMED, CANCELED, MODIFIED
    }
}

