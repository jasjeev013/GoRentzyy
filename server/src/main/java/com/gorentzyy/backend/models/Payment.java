package com.gorentzyy.backend.models;

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
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;

    public enum PaymentMethod {
        CREDIT_CARD, PAYPAL,UPI, OTHER
    }

    public enum PaymentStatus {
        SUCCESSFUL, FAILED, PENDING
    }

    public enum RefundStatus {
        REFUNDED, NON_REFUNDABLE, PENDING
    }
}

