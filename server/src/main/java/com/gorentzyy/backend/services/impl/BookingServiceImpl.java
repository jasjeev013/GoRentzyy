package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.constants.EmailConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.BookingDto;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.BookingService;
import com.gorentzyy.backend.services.EmailService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, CarRepository carRepository, UserRepository userRepository, ModelMapper modelMapper, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    private double calculateTotalPrice(Car car, LocalDateTime startDate, LocalDateTime endDate) {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double totalPrice;

        if (days < 7) {
            totalPrice = days * car.getRentalPricePerDay();
        } else if (days < 30) {
            long weeks = days / 7;
            long remainingDays = days % 7;
            totalPrice = (weeks * car.getRentalPricePerWeek()) + (remainingDays * car.getRentalPricePerDay());
        } else {
            long months = days / 30;
            long remainingDays = days % 30;
            totalPrice = (months * car.getRentalPricePerMonth()) + (remainingDays * car.getRentalPricePerDay());
        }

        return totalPrice;
    }

    private boolean isCarAlreadyBooked(Long carId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Booking> existingBookings = bookingRepository.findByCarIdAndDateRange(carId, startDate, endDate);

        return !existingBookings.isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Retryable(
            value = {DatabaseException.class},  // Retry only for DatabaseException
            maxAttempts = 3,  // Retry 3 times before failing
            backoff = @Backoff(delay = 2000, multiplier = 2)  // 2 sec delay, increasing exponentially
    )
    public ResponseEntity<ApiResponseObject> createBooking(BookingDto bookingDto, String emailId, Long carId) {
        try {
            // Step 1: Validate car existence
            Car car = carRepository.findById(carId).orElseThrow(() -> {
                logger.error("Car with ID {} not found for booking.", carId);
                return new CarNotFoundException("Car not found with ID: " + carId);
            });

            logger.info("Car with ID {} found for booking.", carId);

            // Step 2: Validate renter existence
            User renter = userRepository.findByEmail(emailId).orElseThrow(() -> {
                logger.error("User with Email ID {} not found for booking.", emailId);
                return new UserNotFoundException("User not found with Email ID: " + emailId);
            });

            // Ensure only RENTERS can book a car (not hosts)
            if (renter.getRole() == AppConstants.Role.HOST) {
                logger.error("User with Email ID {} is not authorized to book cars.", emailId);
                throw new RoleNotAuthorizedException("Hosts are not allowed to book cars.");
            }

            logger.info("Renter with Email ID {} found for booking.", emailId);

            // Step 3: Prevent booking conflicts
            if (isCarAlreadyBooked(carId, bookingDto.getStartDate(), bookingDto.getEndDate())) {
                logger.warn("Car ID {} is already booked for the selected date range.", carId);
                throw new BookingConflictException("This car is already booked for the selected date range.");
            }

            // Step 4: Map BookingDto to Booking entity
            Booking booking = modelMapper.map(bookingDto, Booking.class);
            booking.setCar(car);
            booking.setRenter(renter);

            // Set creation and update timestamps
            LocalDateTime now = LocalDateTime.now();
            booking.setCreatedAt(now);
            booking.setUpdatedAt(now);

            // Step 5: Calculate and set the total price based on the booking duration
            booking.setTotalPrice(calculateTotalPrice(car, booking.getStartDate(), booking.getEndDate()));
            logger.info("Total price calculated for booking: {}", booking.getTotalPrice());

            // Step 6: Save the booking
            Booking savedBooking = bookingRepository.save(booking);

            // Step 7: Associate booking with car and renter
            car.getBookings().add(savedBooking);
            renter.getBookings().add(savedBooking);

            // Step 8: Return success response with booking details
            logger.info("Booking created successfully with ID {}", savedBooking.getBookingId());

            emailService.sendEmail(renter.getEmail(),EmailConstants.bookingConfirmedOfRenterSubject(car.getName()), EmailConstants.bookingConfirmedOfRenterBody);
            emailService.sendEmail(car.getHost().getEmail(),EmailConstants.bookingConfirmedOfSpecificCarOfHostSubject(car.getName(),renter.getFullName()),EmailConstants.bookingConfirmedOfRenterBody);


            return new ResponseEntity<>(new ApiResponseObject(
                    "Booking has been established", true, modelMapper.map(savedBooking, BookingDto.class)
            ), HttpStatus.CREATED);

        } catch (CarNotFoundException | UserNotFoundException | RoleNotAuthorizedException | BookingConflictException ex) {
            // Log and rethrow specific exceptions
            logger.error("Error creating booking: {}", ex.getMessage());
            throw ex;  // These are expected exceptions handled by GlobalExceptionHandler

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while creating booking: {}", ex.getMessage());
            throw new DatabaseException("Error while saving the booking to the database.");
        }
    }

    // Status is being changed
    @Override
    public ResponseEntity<ApiResponseObject> updateBooking(BookingDto bookingDto, Long bookingId) {
        try {
            // Step 1: Check if the booking exists
            Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.")
            );
            logger.info("Booking with ID {} found for update.", bookingId);

            // Step 2: Only update the status field (Keep startDate & endDate unchanged)
            if (bookingDto.getStatus() != null) {
                existingBooking.setStatus(bookingDto.getStatus());
            }

            // Update the last modified timestamp
            existingBooking.setUpdatedAt(LocalDateTime.now());

            // Step 3: Save the updated booking
            Booking updatedBooking = bookingRepository.save(existingBooking);
            logger.info("Booking with ID {} successfully updated.", bookingId);

            // Step 4: Return the success response
            return new ResponseEntity<>(new ApiResponseObject(
                    "Booking updated successfully", true, modelMapper.map(updatedBooking, BookingDto.class)
            ), HttpStatus.ACCEPTED);

        } catch (BookingNotFoundException ex) {
            logger.error("Error updating booking: {}", ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            logger.error("Unexpected error while updating booking: {}", ex.getMessage());
            throw new DatabaseException("Error while updating the booking. Please try again.");
        }
    }


    @Override
    public ResponseEntity<ApiResponseObject> getBookingById(Long bookingId) {
        try {
            // Step 1: Check if the booking exists by ID
            Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.")
            );
            logger.info("Booking with ID {} fetched successfully.", bookingId);

            // Step 2: Return the successful response with the booking data
            return new ResponseEntity<>(new ApiResponseObject(
                    "Booking fetched successfully", true, modelMapper.map(existingBooking, BookingDto.class)
            ), HttpStatus.OK);

        } catch (BookingNotFoundException ex) {
            // Log and rethrow the exception to be handled by your global exception handler
            logger.error("Error fetching booking with ID {}: {}", bookingId, ex.getMessage());
            throw ex;  // Exception will be handled by the global exception handler

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching booking with ID {}: {}", bookingId, ex.getMessage());
            throw new DatabaseException("Error while fetching the booking. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> cancelBooking(Long bookingId) {
        try {
            // Step 1: Check if the booking exists
            Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.")
            );

            logger.info("Booking with ID {} found for cancellation.", bookingId);

            // Step 2: Optionally, check if the booking can be cancelled (e.g., if it's already completed)
            if (existingBooking.getStatus() == AppConstants.Status.CONFIRMED) {
                throw new InvalidBookingStateException("Booking has already been completed and cannot be cancelled.");
            }

            // Step 3: Delete the booking (cancel the booking)
            bookingRepository.delete(existingBooking);

            logger.info("Booking with ID {} cancelled successfully.", bookingId);

            // Step 4: Return success response
            return new ResponseEntity<>(new ApiResponseObject(
                    "Booking cancelled successfully", true, null
            ), HttpStatus.OK);  // Use HttpStatus.NO_CONTENT for successful deletions

        } catch (BookingNotFoundException ex) {
            // Log the error and rethrow the exception for global handling
            logger.error("Error cancelling booking with ID {}: {}", bookingId, ex.getMessage());
            throw ex;

        } catch (InvalidBookingStateException ex) {
            // Log the specific error related to invalid state (e.g., already completed)
            logger.error("Error cancelling booking with ID {}: {}", bookingId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Log any unexpected errors
            logger.error("Unexpected error while cancelling booking with ID {}: {}", bookingId, ex.getMessage());
            throw new DatabaseException("Error while cancelling the booking. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByRenter(String emailId) {
        try {
            // Step 1: Check if the renter exists
            User renter = userRepository.findByEmail(emailId).orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + emailId)
            );
            logger.info("Renter with ID {} found.", emailId);

            // Step 2: Fetch bookings for the renter
            List<Booking> bookings = bookingRepository.findByRenter(renter);

            if (bookings.isEmpty()) {
                logger.info("No bookings found for renter with ID {}", emailId);
                return new ResponseEntity<>(new ApiResponseData(
                        "No bookings found for the renter", false, Collections.emptyList()
                ), HttpStatus.NO_CONTENT);  // HTTP 204 when there are no bookings
            }

            // Step 3: Convert the bookings to DTOs
            List<BookingDto> bookingDtos = bookings.stream()
                    .map(booking -> modelMapper.map(booking, BookingDto.class))
                    .toList();

            // Step 4: Return the response with the list of bookings
            return new ResponseEntity<>(new ApiResponseData(
                    "All bookings fetched successfully", true, Collections.singletonList(bookingDtos)
            ), HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            // Log and rethrow the exception
            logger.error("Error fetching bookings for renter with ID {}: {}", emailId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching bookings for renter with ID {}: {}", emailId, ex.getMessage());
            throw new DatabaseException("Error while fetching bookings. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByCar(Long carId) {
        try {
            // Step 1: Validate if the car exists
            Car car = carRepository.findById(carId).orElseThrow(() ->
                    new CarNotFoundException("Car not found with id: " + carId)
            );
            logger.info("Car with ID {} found.", carId);

            // Step 2: Fetch bookings for the car
            List<Booking> bookings = bookingRepository.findByCar(car);

            if (bookings.isEmpty()) {
                logger.info("No bookings found for car with ID {}", carId);
                return new ResponseEntity<>(new ApiResponseData(
                        "No bookings found for this car", false, Collections.emptyList()
                ), HttpStatus.NO_CONTENT);  // Return 204 if no bookings are found
            }

            // Step 3: Convert bookings to BookingDto objects
            List<BookingDto> bookingDtos = bookings.stream()
                    .map(booking -> modelMapper.map(booking, BookingDto.class))
                    .toList();

            // Step 4: Return the list of booking DTOs with success message
            return new ResponseEntity<>(new ApiResponseData(
                    "All bookings fetched successfully", true, Collections.singletonList(bookingDtos)
            ), HttpStatus.OK);

        } catch (CarNotFoundException ex) {
            // Log and rethrow the exception for global handling
            logger.error("Error fetching bookings for car with ID {}: {}", carId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching bookings for car with ID {}: {}", carId, ex.getMessage());
            throw new DatabaseException("Error while fetching bookings. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByHost(String emailId) {
        try {
            // Step 1: Validate that the host exists
            User host = userRepository.findByEmail(emailId).orElseThrow(() ->
                    new UserNotFoundException("User not found with Email id: " + emailId)
            );
            logger.info("Host with ID {} found.", emailId);

            // Step 2: Fetch bookings associated with the host's cars
            List<Booking> bookings = bookingRepository.findByCarHost(host);

            if (bookings.isEmpty()) {
                logger.info("No bookings found for host with ID {}", emailId);
                return new ResponseEntity<>(new ApiResponseData(
                        "No bookings found for this host", false, Collections.emptyList()
                ), HttpStatus.NO_CONTENT);  // HTTP 204 when no bookings found
            }

            // Step 3: Convert bookings to BookingDto objects
            List<BookingDto> bookingDtos = bookings.stream()
                    .map(booking -> modelMapper.map(booking, BookingDto.class))
                    .toList();

            // Step 4: Return the response with the list of bookings
            return new ResponseEntity<>(new ApiResponseData(
                    "All bookings fetched successfully", true, Collections.singletonList(bookingDtos)
            ), HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            // Log and rethrow the exception
            logger.error("Error fetching bookings for host with ID {}: {}", emailId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching bookings for host with ID {}: {}", emailId, ex.getMessage());
            throw new DatabaseException("Error while fetching bookings. Please try again.");
        }
    }



}
