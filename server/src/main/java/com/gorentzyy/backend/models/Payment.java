package com.gorentzyy.backend.models;

import com.gorentzyy.backend.constants.AppConstants;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private LocalDateTime paymentDate;

    private double amount;

    @Enumerated(EnumType.STRING)
    private AppConstants.PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private AppConstants.PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private AppConstants.RefundStatus refundStatus;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}

