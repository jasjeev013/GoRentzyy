package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Payment;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PaymentDto;
import com.gorentzyy.backend.payloads.PaymentVerificationRequest;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.PaymentRepository;
import com.gorentzyy.backend.services.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final String CURRENCY = "INR";
    private static final String PAYMENT_CAPTURE = "1";

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              ModelMapper modelMapper) throws RazorpayException {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
        this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> createRazorpayOrder(Double amount, Long bookingId) {
        logger.info("Creating Razorpay order for booking ID: {}", bookingId);

        try {
            validateAmount(amount);
            Booking booking = getBookingById(bookingId);

            JSONObject orderRequest = buildOrderRequest(amount, booking);
            Order order = razorpayClient.orders.create(orderRequest);

            Payment payment = createPaymentRecord(amount, booking, order);
            paymentRepository.save(payment);

            logger.info("Successfully created Razorpay order ID: {} for booking ID: {}",
                    order.get("id"), bookingId);

            return buildOrderResponse(order);

        } catch (BookingNotFoundException | InvalidPaymentAmountException e) {
            throw e;
        } catch (RazorpayException e) {
            logger.error("Razorpay API error for booking ID: {}", bookingId, e);
            throw new PaymentProcessingException("Failed to create Razorpay order");
        } catch (Exception e) {
            logger.error("Unexpected error creating order for booking ID: {}", bookingId, e);
            throw new PaymentProcessingException("Failed to process payment");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> verifyPayment(PaymentVerificationRequest request) {
        logger.info("Verifying payment for order ID: {}", request.getRazorpayOrderId());

        try {
            validateVerificationRequest(request);
            verifyPaymentSignature(request);

            Payment payment = getPaymentByOrderId(request.getRazorpayOrderId());
            updatePaymentStatus(payment, request);

            Booking booking = payment.getBooking();
            updateBookingStatus(booking);

            logger.info("Successfully verified payment for order ID: {}", request.getRazorpayOrderId());

            return ResponseEntity.ok(new ApiResponseObject(
                    "Payment verified successfully",
                    true,
                    null)
            );

        } catch (PaymentVerificationException | PaymentNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error verifying payment for order ID: {}", request.getRazorpayOrderId(), e);
            throw new PaymentVerificationException("Payment verification failed");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> makePayment(PaymentDto paymentDto, Long bookingId) {
        logger.info("Processing direct payment for booking ID: {}", bookingId);

        try {
            validatePaymentDto(paymentDto);
            Booking booking = getBookingById(bookingId);

            Payment payment = createPaymentFromDto(paymentDto, booking);
            Payment savedPayment = paymentRepository.save(payment);

            if (paymentDto.getPaymentStatus() == AppConstants.PaymentStatus.SUCCESSFUL) {
                updateBookingStatus(booking);
            }

            logger.info("Successfully processed payment ID: {} for booking ID: {}",
                    savedPayment.getPaymentId(), bookingId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseObject(
                            "Payment processed successfully",
                            true,
                            modelMapper.map(savedPayment, PaymentDto.class))
                    );

        } catch (BookingNotFoundException | InvalidPaymentAmountException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error processing payment for booking ID: {}", bookingId, e);
            throw new PaymentProcessingException("Failed to process payment");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updatePaymentStatus(Long paymentId, PaymentDto paymentDto) {
        logger.info("Updating payment status for payment ID: {}", paymentId);

        try {
            validatePaymentStatus(paymentDto);
            Payment payment = getPaymentById(paymentId);

            updatePaymentFromDto(payment, paymentDto);
            Payment savedPayment = paymentRepository.save(payment);

            logger.info("Successfully updated payment ID: {}", paymentId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Payment status updated successfully",
                    true,
                    modelMapper.map(savedPayment, PaymentDto.class))
            );

        } catch (PaymentNotFoundException | InvalidPaymentStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating payment ID: {}", paymentId, e);
            throw new PaymentProcessingException("Failed to update payment status");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getPayment(Long paymentId) {
        logger.info("Fetching payment with ID: {}", paymentId);

        try {
            Payment payment = getPaymentById(paymentId);

            logger.info("Successfully retrieved payment ID: {}", paymentId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Payment retrieved successfully",
                    true,
                    modelMapper.map(payment, PaymentDto.class))
            );

        } catch (PaymentNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching payment ID: {}", paymentId, e);
            throw new DatabaseException("Failed to retrieve payment");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> removePayment(Long paymentId) {
        logger.info("Deleting payment with ID: {}", paymentId);

        try {
            Payment payment = getPaymentById(paymentId);
            paymentRepository.delete(payment);

            logger.info("Successfully deleted payment ID: {}", paymentId);

            return ResponseEntity.ok()
                    .body(new ApiResponseObject(
                            "Payment deleted successfully",
                            true,
                            null)
                    );

        } catch (PaymentNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting payment ID: {}", paymentId, e);
            throw new DatabaseException("Failed to delete payment");
        }
    }

    // Helper methods
    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking not found with ID: {}", bookingId);
                    return new BookingNotFoundException("Booking not found");
                });
    }

    private Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found with ID: {}", paymentId);
                    return new PaymentNotFoundException("Payment not found");
                });
    }

    private Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> {
                    logger.error("Payment not found for order ID: {}", orderId);
                    return new PaymentNotFoundException("Payment not found");
                });
    }

    private void validateAmount(Double amount) {
        if (amount == null || amount <= 0) {
            logger.error("Invalid payment amount: {}", amount);
            throw new InvalidPaymentAmountException("Payment amount must be positive");
        }
    }

    private void validatePaymentDto(PaymentDto paymentDto) {
        if (paymentDto == null) {
            throw new InvalidPaymentDataException("Payment data cannot be null");
        }
        validateAmount(paymentDto.getAmount());
    }

    private void validatePaymentStatus(PaymentDto paymentDto) {
        if (paymentDto.getPaymentStatus() == null) {
            logger.error("Null payment status provided");
            throw new InvalidPaymentStatusException("Payment status cannot be null");
        }
    }

    private void validateVerificationRequest(PaymentVerificationRequest request) {
        if (request == null || !StringUtils.hasText(request.getRazorpayOrderId()) ||
                !StringUtils.hasText(request.getRazorpayPaymentId()) ||
                !StringUtils.hasText(request.getRazorpaySignature())) {
            logger.error("Invalid verification request");
            throw new PaymentVerificationException("Invalid verification data");
        }
    }

    private void verifyPaymentSignature(PaymentVerificationRequest request) {
        String generatedSignature = HmacUtils.hmacSha256Hex(
                razorpayKeySecret,
                request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId()
        );

        if (!generatedSignature.equals(request.getRazorpaySignature())) {
            logger.error("Payment signature verification failed");
            throw new PaymentVerificationException("Invalid signature");
        }
    }

    private JSONObject buildOrderRequest(Double amount, Booking booking) {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Convert to paise
        orderRequest.put("currency", CURRENCY);
        orderRequest.put("receipt", "receipt_" + booking.getBookingId());
        orderRequest.put("payment_capture", PAYMENT_CAPTURE);

        JSONObject notes = new JSONObject();
        notes.put("bookingId", booking.getBookingId().toString());
        notes.put("carModel", booking.getCar().getName());
        orderRequest.put("notes", notes);

        return orderRequest;
    }

    private Payment createPaymentRecord(Double amount, Booking booking, Order order) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setPaymentStatus(AppConstants.PaymentStatus.PENDING);
        payment.setBooking(booking);
        payment.setRazorpayOrderId(order.get("id"));
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    private Payment createPaymentFromDto(PaymentDto paymentDto, Booking booking) {
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        payment.setBooking(booking);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    private void updatePaymentFromDto(Payment payment, PaymentDto paymentDto) {
        payment.setPaymentStatus(paymentDto.getPaymentStatus());
        payment.setRefundStatus(paymentDto.getRefundStatus());
        payment.setUpdatedAt(LocalDateTime.now());
    }

    private void updatePaymentStatus(Payment payment, PaymentVerificationRequest request) {
        payment.setPaymentStatus(AppConstants.PaymentStatus.SUCCESSFUL);
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private void updateBookingStatus(Booking booking) {
        booking.setStatus(AppConstants.Status.CONFIRMED);
        bookingRepository.save(booking);
    }

    private ResponseEntity<ApiResponseObject> buildOrderResponse(Order order) {
        JSONObject response = new JSONObject();
        response.put("orderId", Optional.ofNullable(order.get("id")));
        response.put("amount", Optional.ofNullable(order.get("amount")));
        response.put("currency", Optional.ofNullable(order.get("currency")));
        response.put("key", razorpayKeyId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseObject(
                        "Razorpay order created",
                        true,
                        response.toMap())
                );
    }
}