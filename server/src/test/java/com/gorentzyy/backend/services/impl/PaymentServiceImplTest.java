package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.BookingNotFoundException;
import com.gorentzyy.backend.exceptions.PaymentNotFoundException;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Payment;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PaymentDto;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setBookingId(1L);

        paymentDto = new PaymentDto();
        paymentDto.setPaymentDate(LocalDateTime.now());
        paymentDto.setAmount(500.0);
        paymentDto.setPaymentMethod(AppConstants.PaymentMethod.CREDIT_CARD);
        paymentDto.setPaymentStatus(AppConstants.PaymentStatus.SUCCESSFUL);
        paymentDto.setRefundStatus(AppConstants.RefundStatus.NON_REFUNDABLE);

        payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setPaymentStatus(paymentDto.getPaymentStatus());
        payment.setRefundStatus(paymentDto.getRefundStatus());
    }

    @Test
    void testMakePayment_Success() {
        // Ensure that BookingRepository returns the expected booking
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(modelMapper.map(paymentDto, Payment.class)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        ResponseEntity<ApiResponseObject> response = paymentService.makePayment(paymentDto, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("The Payment Added Successfully", response.getBody().getMessage());

        verify(bookingRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testMakePayment_BookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> {
            paymentService.makePayment(paymentDto, 1L);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testUpdatePaymentStatus_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        ResponseEntity<ApiResponseObject> response = paymentService.updatePaymentStatus(1L, paymentDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The payment has been updated successfully.", response.getBody().getMessage());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testUpdatePaymentStatus_PaymentNotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.updatePaymentStatus(1L, paymentDto);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testGetPayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(paymentDto);

        ResponseEntity<ApiResponseObject> response = paymentService.getPayment(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The payment is found successfully", response.getBody().getMessage());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPayment_NotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPayment(1L);
        });

        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void testRemovePayment_Success() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        ResponseEntity<ApiResponseObject> response = paymentService.removePayment(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(paymentRepository, times(1)).delete(payment);
    }

    @Test
    void testRemovePayment_NotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.removePayment(1L);
        });

        verify(paymentRepository, never()).delete(any(Payment.class));
    }
}