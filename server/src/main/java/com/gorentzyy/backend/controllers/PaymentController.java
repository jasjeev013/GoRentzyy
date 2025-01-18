package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PaymentDto;
import com.gorentzyy.backend.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create/{bookingId}")
    public ResponseEntity<ApiResponseObject> makePayment(@RequestBody PaymentDto paymentDto, @PathVariable Long bookingId){
        return paymentService.makePayment(paymentDto,bookingId);
    }

    @PutMapping("/update/{paymentId}")
    public ResponseEntity<ApiResponseObject> updatePayment(@RequestBody PaymentDto paymentDto,@PathVariable Long paymentId){
        return paymentService.updatePaymentStatus(paymentId,paymentDto);
    }

    @GetMapping("/get/{paymentId}")
    public ResponseEntity<ApiResponseObject> getPayment(@PathVariable Long paymentId){
        return paymentService.getPayment(paymentId);
    }

    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity<ApiResponseObject> deletePayment(@PathVariable Long paymentId){
        return paymentService.removePayment(paymentId);
    }


}
