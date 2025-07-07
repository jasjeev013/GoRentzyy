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

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository, ModelMapper modelMapper) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.modelMapper = modelMapper;
    }
// THe date should be taken on its own


    public ResponseEntity<ApiResponseObject> createRazorpayOrder(Double amount, Long bookingId) throws RazorpayException {
        try {
            // Validate booking exists
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

            // Create Razorpay order
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Convert to paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_" + bookingId);
            orderRequest.put("payment_capture", 1);

            // Add booking details as notes
            JSONObject notes = new JSONObject();
            notes.put("bookingId", bookingId.toString());
            notes.put("carModel", booking.getCar().getName());
            orderRequest.put("notes", notes);

            Order order = razorpay.orders.create(orderRequest);

            // Create payment record in PENDING state
            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setPaymentStatus(AppConstants.PaymentStatus.PENDING);
            payment.setBooking(booking);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setCreatedAt(LocalDateTime.now());

            Payment savedPayment = paymentRepository.save(payment);

            // Return order details to frontend
            JSONObject response = new JSONObject();
            response.put("orderId", Optional.ofNullable(order.get("id")));
            response.put("amount", Optional.ofNullable(order.get("amount")));
            response.put("currency", Optional.ofNullable(order.get("currency")));
            response.put("key", razorpayKeyId);

            return new ResponseEntity<>(new ApiResponseObject(
                    "Razorpay order created", true, response.toMap()),
                    HttpStatus.CREATED);

        } catch (RazorpayException e) {
            logger.error("Razorpay error: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to create Razorpay order");
        }
    }


    public ResponseEntity<ApiResponseObject> verifyPayment(PaymentVerificationRequest request) {
        try {
            // Verify signature
            String generatedSignature = HmacUtils.hmacSha256Hex(
                    razorpayKeySecret,
                    request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId()
            );

            if (!generatedSignature.equals(request.getRazorpaySignature())) {
                throw new PaymentVerificationException("Invalid signature");
            }

            // Update payment status
            Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            payment.setPaymentStatus(AppConstants.PaymentStatus.SUCCESSFUL);
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setUpdatedAt(LocalDateTime.now());

            // Update booking status
            Booking booking = payment.getBooking();
            booking.setStatus(AppConstants.Status.CONFIRMED);

            paymentRepository.save(payment);
            bookingRepository.save(booking);

            return new ResponseEntity<>(new ApiResponseObject(
                    "Payment verified successfully", true, null),
                    HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Payment verification failed: {}", e.getMessage());
            throw new PaymentVerificationException("Payment verification failed");
        }
    }
    @Override
    public ResponseEntity<ApiResponseObject> makePayment(PaymentDto paymentDto, Long bookingId) {
        try {
            // Step 1: Validate if the booking exists
            Booking existingBooking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " not found."));

            logger.info("Booking with ID {} found.", bookingId);

            // Step 2: Validate payment amount (assuming positive and non-zero amounts are required)
            if (paymentDto.getAmount() <= 0) {
                throw new InvalidPaymentAmountException("Payment amount must be greater than zero.");
            }

            // Step 3: Map PaymentDto to Payment entity
            Payment payment = modelMapper.map(paymentDto, Payment.class);

            // Step 4: Link the payment to the existing booking and vice versa
            existingBooking.setPayment(payment);
            payment.setBooking(existingBooking);


            // Step 5: Save the payment and update booking status if necessary
            Payment savedPayment = paymentRepository.save(payment);

            // Optional: If payment is successful, update the booking status
            existingBooking.setStatus(AppConstants.Status.CONFIRMED);  // Or appropriate status
            bookingRepository.save(existingBooking);

            // Step 6: Return the success response with the saved payment details
            logger.info("Payment for booking with ID {} processed successfully.", bookingId);
            return new ResponseEntity<>(new ApiResponseObject(
                    "The Payment Added Successfully", true, modelMapper.map(savedPayment, PaymentDto.class)),
                    HttpStatus.CREATED);

        } catch (BookingNotFoundException | InvalidPaymentAmountException ex) {
            // Let known business exceptions propagate
            throw ex;
        } catch (Exception e) {
            logger.error("Error processing payment for booking with ID {}: {}", bookingId, e.getMessage());
            throw new PaymentProcessingException("Failed to process the payment due to an unexpected error.");
        }

    }

    @Override
    public ResponseEntity<ApiResponseObject> updatePaymentStatus(Long paymentId, PaymentDto paymentDto) {
        try {
            // Step 1: Check if the payment exists in the database
            Payment existingPayment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));

            logger.info("Payment with ID {} found. Updating status...", paymentId);

            // Step 2: Validate the new payment status using the PaymentStatus enum
            if (paymentDto.getPaymentStatus() == null) {
                throw new InvalidPaymentStatusException("Payment status cannot be null.");
            }


            // Step 3: Update the payment status
            existingPayment.setPaymentStatus(paymentDto.getPaymentStatus());
            existingPayment.setRefundStatus(paymentDto.getRefundStatus());

            // Step 4: Save the updated payment record
            Payment savedPayment = paymentRepository.save(existingPayment);

            logger.info("Payment status updated successfully for Payment ID {}", paymentId);

            // Step 5: Return success response
            return new ResponseEntity<>(new ApiResponseObject(
                    "The payment has been updated successfully.", true, modelMapper.map(savedPayment, PaymentDto.class)),
                    HttpStatus.OK);

        } catch (PaymentNotFoundException ex) {
            // Log and rethrow the exception for centralized handling
            logger.error("Payment not found for ID {}: {}", paymentId, ex.getMessage());
            throw ex;
        } catch (InvalidPaymentStatusException ex) {
            // Handle invalid payment status
            logger.error("Invalid payment status provided for Payment ID {}: {}", paymentId, ex.getMessage());
            throw ex;
        } catch (Exception e) {
            // Log unexpected errors and throw a generic exception
            logger.error("Error updating payment status for Payment ID {}: {}", paymentId, e.getMessage());
            throw new PaymentProcessingException("Failed to update payment status due to an unexpected error.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getPayment(Long paymentId) {
        try {
            // Fetch the existing payment
            Payment existingPayment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));

            // Log the request (optional)
            logger.info("Payment with ID {} fetched successfully.", paymentId);

            // Return the response with payment details
            return new ResponseEntity<>(new ApiResponseObject(
                    "The payment is found successfully", true, modelMapper.map(existingPayment, PaymentDto.class)),
                    HttpStatus.OK);

        } catch (PaymentNotFoundException ex) {
            // Specific exception handling (will be caught by Global Exception Handler if you have one)
            logger.error("Payment not found for ID {}: {}", paymentId, ex.getMessage());
            throw ex;  // Rethrow to be caught by the global handler
        } catch (Exception e) {
            // Handle any unexpected exceptions
            logger.error("Error fetching payment for ID {}: {}", paymentId, e.getMessage());
            throw new DatabaseException("An unexpected error occurred while fetching the payment.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> removePayment(Long paymentId) {
        try {
            // Fetch the payment by ID
            Payment existingPayment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment with ID " + paymentId + " not found."));

            // Log the deletion attempt
            logger.info("Attempting to delete Payment with ID: {}", paymentId);

            // Delete the payment from the repository
            paymentRepository.delete(existingPayment);

            // Log successful deletion
            logger.info("Payment with ID {} deleted successfully.", paymentId);

            // Return a success response
            return new ResponseEntity<>(new ApiResponseObject(
                    "The Payment is deleted successfully", true, null), HttpStatus.NO_CONTENT);  // Consider using 204 status for deletion

        } catch (PaymentNotFoundException ex) {
            // Handle specific PaymentNotFoundException (will be caught by your global exception handler)
            logger.error("Payment not found for ID {}: {}", paymentId, ex.getMessage());
            throw ex;  // Rethrow to let the global handler handle it
        } catch (Exception e) {
            // Handle any other unexpected exceptions (e.g., database issues)
            logger.error("Error deleting payment with ID {}: {}", paymentId, e.getMessage());
            throw new DatabaseException("An unexpected error occurred while deleting the payment.");
        }
    }
}
