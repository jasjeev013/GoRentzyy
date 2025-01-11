package com.gorentzyy.backend.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    private Long paymentId;


    private LocalDateTime paymentDate;
    private double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private RefundStatus refundStatus;
    /*
    private Booking booking;
    */


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
