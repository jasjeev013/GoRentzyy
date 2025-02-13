package com.gorentzyy.backend.advice;

import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.ErrorResponse;
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
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The user with this email already exists.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The input data is invalid.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordHashingException.class)
    public ResponseEntity<ErrorResponse> handlePasswordHashingException(PasswordHashingException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Password hashing failed.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "There was an error saving the user to the database.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "There was an error saving the user to the database.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseObject> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ApiResponseObject(errors, false, null));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred.", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The user with the specified ID does not exist.");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(CarNotFoundException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The car with the specified ID does not exist.");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotAuthorizedException(RoleNotAuthorizedException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCarDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCarDataException(InvalidCarDataException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBookingDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingDateException(InvalidBookingDateException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidBookingStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingStateException(InvalidBookingStateException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPaymentAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentAmountException(InvalidPaymentAmountException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPaymentStatusException(InvalidPaymentStatusException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ReviewCreationException.class)
    public ResponseEntity<ErrorResponse> handleReviewCreationException(ReviewCreationException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReviewUpdateException.class)
    public ResponseEntity<ErrorResponse> handleReviewUpdateException(ReviewUpdateException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The role is not authorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCarDataException(BookingNotFoundException ex) {
        // Provide a clear, specific message to the client
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "The booking is not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<String> handlePaymentProcessingException(PaymentProcessingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<String> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiResponseObject> handleNotificationNotFoundException(NotificationNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponseObject(ex.getMessage(), false, null), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ApiResponseObject> handleReviewNotFoundException(ReviewNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponseObject(ex.getMessage(), false, null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingConflictException.class)
    public ResponseEntity<ApiResponseObject> handleBookingConflictException(BookingConflictException ex) {
        return new ResponseEntity<>(new ApiResponseObject(ex.getMessage(), false, null), HttpStatus.NOT_FOUND);
    }


}
