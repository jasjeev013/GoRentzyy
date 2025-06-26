package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PaymentDto;
import com.gorentzyy.backend.payloads.PaymentVerificationRequest;
import com.razorpay.RazorpayException;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<ApiResponseObject> createRazorpayOrder(Double amount, Long bookingId) throws RazorpayException;
    ResponseEntity<ApiResponseObject> verifyPayment(PaymentVerificationRequest request);
    ResponseEntity<ApiResponseObject> makePayment(PaymentDto paymentDto,Long bookingId);
    ResponseEntity<ApiResponseObject> updatePaymentStatus(Long paymentId,PaymentDto paymentDto);

    ResponseEntity<ApiResponseObject> getPayment(Long paymentId);
    ResponseEntity<ApiResponseObject> removePayment(Long paymentId);
}
