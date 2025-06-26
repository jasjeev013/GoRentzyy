package com.gorentzyy.backend.payloads;

import com.gorentzyy.backend.constants.AppConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long paymentId;

    @PastOrPresent(message = "Payment date must be in the past or present")
    private LocalDateTime paymentDate;

    @Positive(message = "Amount must be a positive value")
    private double amount;

    @NotNull(message = "Payment method is required")
    private AppConstants.PaymentMethod paymentMethod;

    @NotNull(message = "Payment status is required")
    private AppConstants.PaymentStatus paymentStatus;

    @NotNull(message = "Refund status is required")
    private AppConstants.RefundStatus refundStatus;
    /*
    private Booking booking;
    */
    private String razorpayOrderId;
    private String razorpayPaymentId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
