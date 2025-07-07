package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.BookingNotFoundException;
import com.gorentzyy.backend.exceptions.InvalidPaymentAmountException;
import com.gorentzyy.backend.exceptions.PaymentVerificationException;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Payment;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PaymentDto;
import com.gorentzyy.backend.payloads.PaymentVerificationRequest;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.OrderClient;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RazorpayClient razorpayClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentDto paymentDto;
    private Payment payment;
    private Booking booking;
    private PaymentVerificationRequest verificationRequest;

    @BeforeEach
    void setUp() throws RazorpayException {
        // Initialize test data
        paymentDto = new PaymentDto();
        paymentDto.setPaymentId(1L);
        paymentDto.setAmount(100.0);
        paymentDto.setPaymentMethod(AppConstants.PaymentMethod.CREDIT_CARD);
        paymentDto.setPaymentStatus(AppConstants.PaymentStatus.PENDING);
        paymentDto.setRefundStatus(AppConstants.RefundStatus.NON_REFUNDABLE);

        payment = new Payment();
        payment.setPaymentId(1L);
        payment.setAmount(100.0);
        payment.setPaymentMethod(AppConstants.PaymentMethod.CREDIT_CARD);
        payment.setPaymentStatus(AppConstants.PaymentStatus.PENDING);
        payment.setRefundStatus(AppConstants.RefundStatus.NON_REFUNDABLE);

        Car car = new Car();
        car.setName("Test Car");

        booking = new Booking();
        booking.setBookingId(1L);
        booking.setCar(car);
        booking.setStatus(AppConstants.Status.PENDING);

        verificationRequest = new PaymentVerificationRequest();
        verificationRequest.setRazorpayOrderId("order_123");
        verificationRequest.setRazorpayPaymentId("pay_123");
        verificationRequest.setRazorpaySignature("valid_signature");

        // Set up Razorpay test values
        ReflectionTestUtils.setField(paymentService, "razorpayKeyId", "test_key_id");
        ReflectionTestUtils.setField(paymentService, "razorpayKeySecret", "test_key_secret");
    }

    @Test
    void makePayment_WhenValidInput_ShouldCreatePayment() {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(modelMapper.map(paymentDto, Payment.class)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(paymentDto);

        // Act
        ResponseEntity<ApiResponseObject> response = paymentService.makePayment(paymentDto, 1L);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Payment Added Successfully", response.getBody().getMessage());

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void makePayment_WhenBookingNotFound_ShouldThrowException() {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookingNotFoundException.class, () ->
                paymentService.makePayment(paymentDto, 1L));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void makePayment_WhenInvalidAmount_ShouldThrowException() {
        // Arrange
        paymentDto.setAmount(0);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        // Act & Assert
        assertThrows(InvalidPaymentAmountException.class, () ->
                paymentService.makePayment(paymentDto, 1L));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void updatePaymentStatus_WhenValidInput_ShouldUpdatePayment() {
        // Arrange
        PaymentDto updateDto = new PaymentDto();
        updateDto.setPaymentStatus(AppConstants.PaymentStatus.SUCCESSFUL);

        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(updateDto);

        // Act
        ResponseEntity<ApiResponseObject> response = paymentService.updatePaymentStatus(1L, updateDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The payment has been updated successfully.", response.getBody().getMessage());
        assertEquals(AppConstants.PaymentStatus.SUCCESSFUL, ((PaymentDto) response.getBody().getObject()).getPaymentStatus());

        verify(paymentRepository, times(1)).findById(anyLong());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void getPayment_WhenPaymentExists_ShouldReturnPayment() {
        // Arrange
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(modelMapper.map(payment, PaymentDto.class)).thenReturn(paymentDto);

        // Act
        ResponseEntity<ApiResponseObject> response = paymentService.getPayment(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The payment is found successfully", response.getBody().getMessage());
        assertEquals(paymentDto, response.getBody().getObject());

        verify(paymentRepository, times(1)).findById(anyLong());
    }

    @Test
    void removePayment_WhenPaymentExists_ShouldDeletePayment() {
        // Arrange
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        doNothing().when(paymentRepository).delete(any(Payment.class));

        // Act
        ResponseEntity<ApiResponseObject> response = paymentService.removePayment(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Payment is deleted successfully", response.getBody().getMessage());

        verify(paymentRepository, times(1)).findById(anyLong());
        verify(paymentRepository, times(1)).delete(any(Payment.class));
    }

    @Test
    void createRazorpayOrder_WhenValidInput_ShouldCreateOrder() throws Exception {
        // Arrange
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Mock the Order object returned by Razorpay
        Order mockOrder = mock(Order.class);
        when(mockOrder.get("id")).thenReturn("order_123");
        when(mockOrder.get("amount")).thenReturn(10000);
        when(mockOrder.get("currency")).thenReturn("INR");

        // Mock the RazorpayClient.orders.create(...) call
        OrderClient mockOrderClient = mock(OrderClient.class);
        when(mockOrderClient.create(any(JSONObject.class))).thenReturn(mockOrder);
        when(razorpayClient.orders).thenReturn(mockOrderClient);  // <- this line is key

        // Act
        ResponseEntity<ApiResponseObject> response = paymentService.createRazorpayOrder(100.0, 1L);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Razorpay order created", response.getBody().getMessage());

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(mockOrderClient, times(1)).create(any(JSONObject.class));
    }



//    @Test
    void verifyPayment_WhenValidSignature_ShouldVerifyPayment() {
        // Arrange
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setRazorpayOrderId("order_123");

        when(paymentRepository.findByRazorpayOrderId(anyString())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Act
        ResponseEntity<ApiResponseObject> response = paymentService.verifyPayment(verificationRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Payment verified successfully", response.getBody().getMessage());

        verify(paymentRepository, times(1)).findByRazorpayOrderId(anyString());
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void verifyPayment_WhenInvalidSignature_ShouldThrowException() {
        // Arrange
        verificationRequest.setRazorpaySignature("invalid_signature");

        // Act & Assert
        assertThrows(PaymentVerificationException.class, () ->
                paymentService.verifyPayment(verificationRequest));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}