package com.gorentzyy.backend.advice;

import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    // Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseObject> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.error("Validation Error: {}", errors);
        return ResponseEntity.badRequest().body(new ApiResponseObject(errors, false, null));
    }

    // Handle User-related exceptions
    @ExceptionHandler({ UserAlreadyExistsException.class, UserNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleUserExceptions(RuntimeException ex) {
        HttpStatus status = (ex instanceof UserNotFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        logger.error("User Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "User-related error occurred."), status);
    }

    // Handle Car-related exceptions
    @ExceptionHandler({ CarNotFoundException.class, InvalidCarDataException.class })
    public ResponseEntity<ErrorResponse> handleCarExceptions(RuntimeException ex) {
        HttpStatus status = (ex instanceof CarNotFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        logger.error("Car Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "Car-related error occurred."), status);
    }

    // Handle Booking-related exceptions
    @ExceptionHandler({ BookingNotFoundException.class, InvalidBookingDateException.class, InvalidBookingStateException.class, BookingConflictException.class })
    public ResponseEntity<ErrorResponse> handleBookingExceptions(RuntimeException ex) {
        HttpStatus status = (ex instanceof BookingNotFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        logger.error("Booking Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "Booking-related error occurred."), status);
    }

    // Handle Payment-related exceptions
    @ExceptionHandler({ InvalidPaymentAmountException.class, InvalidPaymentStatusException.class, PaymentProcessingException.class, PaymentNotFoundException.class })
    public ResponseEntity<ErrorResponse> handlePaymentExceptions(RuntimeException ex) {
        HttpStatus status = (ex instanceof PaymentNotFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        logger.error("Payment Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "Payment-related error occurred."), status);
    }

    // Handle Review-related exceptions
    @ExceptionHandler({ ReviewNotFoundException.class, ReviewCreationException.class, ReviewUpdateException.class })
    public ResponseEntity<ErrorResponse> handleReviewExceptions(RuntimeException ex) {
        HttpStatus status = (ex instanceof ReviewNotFoundException) ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        logger.error("Review Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "Review-related error occurred."), status);
    }

    // Handle Notification-related exceptions
    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotificationExceptions(NotificationNotFoundException ex) {
        logger.error("Notification Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "Notification-related error occurred."), HttpStatus.NOT_FOUND);
    }

    // Handle Authorization Errors
    @ExceptionHandler(RoleNotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotAuthorizedException(RoleNotAuthorizedException ex) {
        logger.error("Authorization Error: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "You are not authorized to perform this action."), HttpStatus.FORBIDDEN);
    }

    // Handle Database-related exceptions
    @ExceptionHandler({ DatabaseException.class, ObjectOptimisticLockingFailureException.class })
    public ResponseEntity<ErrorResponse> handleDatabaseExceptions(RuntimeException ex) {
        logger.error("Database Error: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "A database error occurred."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle Password Hashing Exception
    @ExceptionHandler(PasswordHashingException.class)
    public ResponseEntity<ErrorResponse> handlePasswordHashingException(PasswordHashingException ex) {
        logger.error("Password Hashing Error: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "An error occurred while hashing the password."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle Generic Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("An unexpected error occurred.", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
