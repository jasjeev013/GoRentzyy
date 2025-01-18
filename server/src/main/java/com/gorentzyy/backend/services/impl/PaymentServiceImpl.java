package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.BookingNotFoundException;
import com.gorentzyy.backend.exceptions.PaymentNotFoundException;
import com.gorentzyy.backend.exceptions.PaymentProcessingException;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Payment;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PaymentDto;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.PaymentRepository;
import com.gorentzyy.backend.services.PaymentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository, ModelMapper modelMapper) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
    }




    @Override
    public ResponseEntity<ApiResponseObject> makePayment(PaymentDto paymentDto, Long bookingId) {
        // Validate if the booking exists
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

        try {
            // Map PaymentDto to Payment entity
            Payment payment = modelMapper.map(paymentDto, Payment.class);

            // Link the payment to the existing booking
            existingBooking.setPayment(payment);
            payment.setBooking(existingBooking);

            // Try saving the payment and booking relationship
            Payment savedPayment = paymentRepository.save(payment);

            // Return success response if the payment was saved successfully
            return new ResponseEntity<>(new ApiResponseObject(
                    "The Payment Added Successfully", true, modelMapper.map(savedPayment, PaymentDto.class)),
                    HttpStatus.CREATED);

        } catch (Exception e) {
            // Generic exception handler to catch any unexpected errors
            throw new PaymentProcessingException("Failed to process the payment due to an unexpected error.");
        }
    }



    @Override
    public ResponseEntity<ApiResponseObject> updatePaymentStatus(Long paymentId, PaymentDto paymentDto) {
        try {
            // Check if the payment exists in the database
            Payment existingPayment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));

            // Update payment status
//            existingPayment.setPaymentStatus(paymentDto.getPaymentStatus());
//            existingPayment.setRefundStatus(paymentDto.getRefundStatus());

            // Save the updated payment record
            Payment savedPayment = paymentRepository.save(existingPayment);

            // Return success response
            return new ResponseEntity<>(new ApiResponseObject(
                    "The payment has been updated", true, modelMapper.map(savedPayment, PaymentDto.class)),
                    HttpStatus.OK);
        } catch (PaymentNotFoundException ex) {
            // If the payment is not found, it will be handled by the exception handler
            throw ex;
        } catch (Exception e) {
            // If any other exception occurs, throw a generic payment processing exception
            throw new PaymentProcessingException("Failed to update payment status.");
        }
    }


    @Override
    public ResponseEntity<ApiResponseObject> getPayment(Long paymentId) {
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));
        return new ResponseEntity<>(new ApiResponseObject(
                "The payment is found successfully",true,modelMapper.map(existingPayment,PaymentDto.class)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseObject> removePayment(Long paymentId) {
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));

        paymentRepository.delete(existingPayment);
        return new ResponseEntity<>(new ApiResponseObject(
                "The Payment is deleted Successfully",true,null),HttpStatus.OK);
    }
}
