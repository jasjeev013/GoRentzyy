package com.gorentzyy.backend.payloads;

import com.gorentzyy.backend.constants.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
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



}
