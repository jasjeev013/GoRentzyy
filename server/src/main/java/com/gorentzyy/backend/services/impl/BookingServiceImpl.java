package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, CarRepository carRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public double calculateTotalPrice(Car car, LocalDateTime startDate, LocalDateTime endDate) {
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
// Make Sure booking start n end dte differ by 1-2 days
    // Booking of one cars specifc duration cannot be booked by other

    @Override
    public ResponseEntity<ApiResponseObject> createBooking(BookingDto bookingDto, Long renterId, Long carId) {
        try {
            // Step 1: Validate car existence
            Car car = carRepository.findById(carId).orElseThrow(() ->
                    new CarNotFoundException("Car not found with id: " + carId)
            );
            logger.info("Car with ID {} found for booking.", carId);

            // Step 2: Validate renter existence
            User renter = userRepository.findById(renterId).orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + renterId)
            );

            // Check if the role is authorized (e.g., should be "HOST")
            if (renter.getRole() == AppConstants.Role.HOST) {
                logger.error("User with ID {} is not authorized to add cars.", renterId);
                throw new RoleNotAuthorizedException("Role Not Authorized to add cars");
            }


            logger.info("Renter with ID {} found for booking.", renterId);

            // Step 3: Map the BookingDto to the Booking entity
            Booking booking = modelMapper.map(bookingDto, Booking.class);
            booking.setCar(car);
            booking.setRenter(renter);

            // Set creation and update timestamps
            LocalDateTime now = LocalDateTime.now();
            booking.setCreatedAt(now);
            booking.setUpdatedAt(now);

            // Set the total price based on the booking duration
            booking.setTotalPrice(calculateTotalPrice(car, booking.getStartDate(), booking.getEndDate()));
            logger.info("Total price calculated for booking: {}", booking.getTotalPrice());

            // Step 4: Attempt to save the booking
            Booking savedBooking = bookingRepository.save(booking);

            // Add the booking to car and renter relationships
            car.getBookings().add(savedBooking);
            renter.getBookings().add(savedBooking);

            // Step 5: Return success response with booking details
            logger.info("Booking created successfully with ID {}", savedBooking.getBookingId());
            return new ResponseEntity<>(new ApiResponseObject(
                    "Booking has been established", true, modelMapper.map(savedBooking, BookingDto.class)
            ), HttpStatus.CREATED);

        } catch (CarNotFoundException | UserNotFoundException ex) {
            // Log and rethrow specific exceptions for better clarity
            logger.error("Error creating booking: {}", ex.getMessage());
            throw ex;  // These are expected exceptions that will be handled by your GlobalExceptionHandler

        } catch (Exception ex) {
            // Log any other unexpected errors
            logger.error("Unexpected error while creating booking: {}", ex.getMessage());
            throw new DatabaseException("Error while saving the booking to the database.");
        }
    }
// Booking start Date and end date cannot be changed.. Status can also be not changed
    @Override
    public ResponseEntity<ApiResponseObject> updateBooking(BookingDto bookingDto, Long bookingId) {
        try {
            // Step 1: Check if the booking exists
            Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.")
            );
            logger.info("Booking with ID {} found for update.", bookingId);

            // Step 2: Validate and update the start and end dates
            if (bookingDto.getStartDate().isBefore(LocalDateTime.now())) {
                throw new InvalidBookingDateException("Booking start date cannot be in the past.");
            }
            if (bookingDto.getEndDate().isBefore(bookingDto.getStartDate())) {
                throw new InvalidBookingDateException("Booking end date cannot be before the start date.");
            }

            // Step 3: Only update the fields if they have changed
            boolean isUpdated = false;

            if (existingBooking.getStartDate().isBefore(bookingDto.getStartDate())) {
                existingBooking.setStartDate(bookingDto.getStartDate());
                isUpdated = true;
            }
            if (existingBooking.getEndDate().isBefore(bookingDto.getEndDate())) {
                existingBooking.setEndDate(bookingDto.getEndDate());
                isUpdated = true;
            }
            if (!existingBooking.getStatus().equals(bookingDto.getStatus())) {
                existingBooking.setStatus(bookingDto.getStatus());
                isUpdated = true;
            }

            // Update timestamp for the update
            if (isUpdated) {
                LocalDateTime now = LocalDateTime.now();
                existingBooking.setUpdatedAt(now);
            }

            // Step 4: Save the updated booking
            Booking updatedBooking = bookingRepository.save(existingBooking);
            logger.info("Booking with ID {} successfully updated.", bookingId);

            // Step 5: Return the success response
            return new ResponseEntity<>(new ApiResponseObject(
                    "Booking updated successfully", true, modelMapper.map(updatedBooking, BookingDto.class)
            ), HttpStatus.ACCEPTED);

        } catch (BookingNotFoundException ex) {
            // Log and rethrow the exception to be handled by the global exception handler
            logger.error("Error updating booking: {}", ex.getMessage());
            throw ex;

        } catch (InvalidBookingDateException ex) {
            // Log and handle invalid date exceptions separately
            logger.error("Invalid date error: {}", ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while updating booking: {}", ex.getMessage());
            throw new DatabaseException("Error while updating the booking. Please try again.");
        }
    }
// No Content Issues Below this in all functions
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
            ), HttpStatus.OK);  // Use HttpStatus.OK for a successful GET request

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
            ), HttpStatus.NO_CONTENT);  // Use HttpStatus.NO_CONTENT for successful deletions

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
    public ResponseEntity<ApiResponseData> getBookingsByRenter(Long renterId) {
        try {
            // Step 1: Check if the renter exists
            User renter = userRepository.findById(renterId).orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + renterId)
            );
            logger.info("Renter with ID {} found.", renterId);

            // Step 2: Fetch bookings for the renter
            List<Booking> bookings = bookingRepository.findByRenter(renter);

            if (bookings.isEmpty()) {
                logger.info("No bookings found for renter with ID {}", renterId);
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
            logger.error("Error fetching bookings for renter with ID {}: {}", renterId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching bookings for renter with ID {}: {}", renterId, ex.getMessage());
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
    public ResponseEntity<ApiResponseData> getBookingsByHost(Long hostId) {
        try {
            // Step 1: Validate that the host exists
            User host = userRepository.findById(hostId).orElseThrow(() ->
                    new UserNotFoundException("User not found with id: " + hostId)
            );
            logger.info("Host with ID {} found.", hostId);

            // Step 2: Fetch bookings associated with the host's cars
            List<Booking> bookings = bookingRepository.findByCarHost(host);

            if (bookings.isEmpty()) {
                logger.info("No bookings found for host with ID {}", hostId);
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
            logger.error("Error fetching bookings for host with ID {}: {}", hostId, ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching bookings for host with ID {}: {}", hostId, ex.getMessage());
            throw new DatabaseException("Error while fetching bookings. Please try again.");
        }
    }



}
