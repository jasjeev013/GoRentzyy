package com.gorentzyy.backend.payloads;

import com.gorentzyy.backend.config.AppConstants;
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
    private AppConstants.PaymentMethod paymentMethod;
    private AppConstants.PaymentStatus paymentStatus;
    private AppConstants.RefundStatus refundStatus;
    /*
    private Booking booking;
    */



}
