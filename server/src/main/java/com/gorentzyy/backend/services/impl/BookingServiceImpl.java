package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.constants.EmailConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.*;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.BookingService;
import com.gorentzyy.backend.services.EmailService;
import com.gorentzyy.backend.services.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String CACHE_KEY_PREFIX = "booking:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final RedisService redisService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, CarRepository carRepository,
                              UserRepository userRepository, ModelMapper modelMapper,
                              EmailService emailService, RedisService redisService) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
        this.redisService = redisService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Retryable(
            value = {DatabaseException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public ResponseEntity<ApiResponseObject> createBooking(BookingDto bookingDto, String emailId, Long carId) {
        log.info("Creating booking for car ID: {} by user: {}", carId, emailId);

        try {
            // Validate car exists
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> {
                        log.error("Car not found with ID: {}", carId);
                        return new CarNotFoundException("Car not found with ID: " + carId);
                    });
            log.debug("Car found with ID: {}", carId);

            // Validate user exists and is a renter
            User renter = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", emailId);
                        return new UserNotFoundException("User not found with Email ID: " + emailId);
                    });

            if (renter.getRole() == AppConstants.Role.HOST) {
                log.error("Host attempted to book a car. Email: {}", emailId);
                throw new RoleNotAuthorizedException("Hosts are not allowed to book cars.");
            }
            log.debug("Valid renter found with email: {}", emailId);

            // Check booking conflicts
            if (isCarAlreadyBooked(carId, bookingDto.getStartDate(), bookingDto.getEndDate())) {
                log.warn("Booking conflict detected for car ID: {} between {} and {}",
                        carId, bookingDto.getStartDate(), bookingDto.getEndDate());
                throw new BookingConflictException("This car is already booked for the selected date range.");
            }

            // Create and save booking
            Booking booking = modelMapper.map(bookingDto, Booking.class);
            booking.setCar(car);
            booking.setRenter(renter);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());
            booking.setTotalPrice(calculateTotalPrice(car, booking.getStartDate(), booking.getEndDate()));

            log.debug("Calculated total price: {}", booking.getTotalPrice());

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Booking created successfully with ID: {}", savedBooking.getBookingId());

            // Update relationships
            car.getBookings().add(savedBooking);
            renter.getBookings().add(savedBooking);

            // Send confirmation emails asynchronously
            sendBookingConfirmationEmails(savedBooking, car, renter);

            // Invalidate relevant caches
            invalidateBookingCaches(savedBooking);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseObject(
                            "Booking has been established",
                            true,
                            modelMapper.map(savedBooking, BookingDto.class)
                    ));

        } catch (CarNotFoundException | UserNotFoundException |
                 RoleNotAuthorizedException | BookingConflictException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            log.error("Unexpected error creating booking for car ID: {} by user: {}", carId, emailId, e);
            throw new DatabaseException("Error while saving the booking to the database.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> updateBooking(BookingDto bookingDto, Long bookingId) {
        log.info("Updating booking with ID: {}", bookingId);

        try {
            Booking existingBooking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> {
                        log.error("Booking not found with ID: {}", bookingId);
                        return new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.");
                    });
            log.debug("Found booking with ID: {}", bookingId);

            // Only update status if provided
            if (bookingDto.getStatus() != null) {
                existingBooking.setStatus(bookingDto.getStatus());
                existingBooking.setUpdatedAt(LocalDateTime.now());
                log.debug("Updated booking status to: {}", bookingDto.getStatus());
            }

            Booking updatedBooking = bookingRepository.save(existingBooking);
            log.info("Booking with ID {} updated successfully", bookingId);

            // Invalidate caches
            invalidateBookingCaches(updatedBooking);

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new ApiResponseObject(
                            "Booking updated successfully",
                            true,
                            modelMapper.map(updatedBooking, BookingDto.class)
                    ));

        } catch (BookingNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error updating booking with ID: {}", bookingId, e);
            throw new DatabaseException("Error while updating the booking. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getBookingById(Long bookingId) {
        log.info("Fetching booking with ID: {}", bookingId);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + bookingId;
            if (redisService != null) {
                Optional<BookingDto> cachedBooking = redisService.get(cacheKey, BookingDto.class);
                if (cachedBooking.isPresent()) {
                    log.debug("Cache hit for booking ID: {}", bookingId);
                    return ResponseEntity.ok(
                            new ApiResponseObject("Booking found in cache", true, cachedBooking.get())
                    );
                }
            }

            // Cache miss - fetch from database
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> {
                        log.error("Booking not found with ID: {}", bookingId);
                        return new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.");
                    });
            log.debug("Found booking with ID: {}", bookingId);

            BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);

            // Cache asynchronously
            cacheBookingData(cacheKey, bookingDto);

            return ResponseEntity.ok(
                    new ApiResponseObject(
                            "Booking fetched successfully",
                            true,
                            bookingDto
                    ));

        } catch (BookingNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error fetching booking with ID: {}", bookingId, e);
            throw new DatabaseException("Error while fetching the booking. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> cancelBooking(Long bookingId) {
        log.info("Cancelling booking with ID: {}", bookingId);

        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> {
                        log.error("Booking not found with ID: {}", bookingId);
                        return new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.");
                    });
            log.debug("Found booking with ID: {}", bookingId);

            if (booking.getStatus() == AppConstants.Status.CONFIRMED) {
                log.warn("Attempt to cancel already confirmed booking with ID: {}", bookingId);
                throw new InvalidBookingStateException("Booking has already been completed and cannot be cancelled.");
            }

            bookingRepository.delete(booking);
            log.info("Booking with ID {} cancelled successfully", bookingId);

            // Invalidate caches
            invalidateBookingCaches(booking);

            return ResponseEntity.ok(
                    new ApiResponseObject(
                            "Booking cancelled successfully",
                            true,
                            null
                    ));

        } catch (BookingNotFoundException | InvalidBookingStateException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            log.error("Unexpected error cancelling booking with ID: {}", bookingId, e);
            throw new DatabaseException("Error while cancelling the booking. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByRenter(String emailId) {
        log.info("Fetching bookings for renter: {}", emailId);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + "renter:" + emailId;
            if (redisService != null) {
                Optional<List<BookingDtoRenter>> cachedBookings = redisService.getList(cacheKey, BookingDtoRenter.class);
                if (cachedBookings.isPresent() && !cachedBookings.get().isEmpty()) {
                    log.debug("Cache hit for renter bookings: {}", emailId);
                    return ResponseEntity.ok(
                            new ApiResponseData(
                                    "Bookings found in cache",
                                    true,
                                    Collections.singletonList(cachedBookings.get())
                            )
                    );
                }
            }

            // Cache miss - fetch from database
            User renter = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> {
                        log.error("Renter not found with email: {}", emailId);
                        return new UserNotFoundException("User not found with id: " + emailId);
                    });
            log.debug("Found renter with email: {}", emailId);

            List<Booking> bookings = bookingRepository.findByRenter(renter);

            if (bookings.isEmpty()) {
                log.info("No bookings found for renter: {}", emailId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponseData(
                                "No bookings found for the renter",
                                false,
                                Collections.emptyList()
                        ));
            }

            List<BookingDtoRenter> bookingDtos = bookings.stream()
                    .map(booking -> modelMapper.map(booking, BookingDtoRenter.class))
                    .toList();

            // Cache asynchronously
            cacheBookingList(cacheKey, bookingDtos);

            return ResponseEntity.ok(
                    new ApiResponseData(
                            "All bookings fetched successfully",
                            true,
                            Collections.singletonList(bookingDtos)
                    ));

        } catch (UserNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error fetching bookings for renter: {}", emailId, e);
            throw new DatabaseException("Error while fetching bookings. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByCar(Long carId) {
        log.info("Fetching bookings for car ID: {}", carId);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + "car:" + carId;
            if (redisService != null) {
                Optional<List<BookingDto>> cachedBookings = redisService.getList(cacheKey, BookingDto.class);
                if (cachedBookings.isPresent() && !cachedBookings.get().isEmpty()) {
                    log.debug("Cache hit for car bookings: {}", carId);
                    return ResponseEntity.ok(
                            new ApiResponseData(
                                    "Bookings found in cache",
                                    true,
                                    Collections.singletonList(cachedBookings.get())
                            )
                    );
                }
            }

            // Cache miss - fetch from database
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> {
                        log.error("Car not found with ID: {}", carId);
                        return new CarNotFoundException("Car not found with id: " + carId);
                    });
            log.debug("Found car with ID: {}", carId);

            List<Booking> bookings = bookingRepository.findByCar(car);

            if (bookings.isEmpty()) {
                log.info("No bookings found for car ID: {}", carId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponseData(
                                "No bookings found for this car",
                                false,
                                Collections.emptyList()
                        ));
            }

            List<BookingDto> bookingDtos = bookings.stream()
                    .map(booking -> modelMapper.map(booking, BookingDto.class))
                    .toList();

            // Cache asynchronously
            cacheBookingList(cacheKey, bookingDtos);

            return ResponseEntity.ok(
                    new ApiResponseData(
                            "All bookings fetched successfully",
                            true,
                            Collections.singletonList(bookingDtos)
                    ));

        } catch (CarNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error fetching bookings for car ID: {}", carId, e);
            throw new DatabaseException("Error while fetching bookings. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByHost(String emailId) {
        log.info("Fetching bookings for host: {}", emailId);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + "host:" + emailId;
            if (redisService != null) {
                Optional<List<BookingDto>> cachedBookings = redisService.getList(cacheKey, BookingDto.class);
                if (cachedBookings.isPresent() && !cachedBookings.get().isEmpty()) {
                    log.debug("Cache hit for host bookings: {}", emailId);
                    return ResponseEntity.ok(
                            new ApiResponseData(
                                    "Bookings found in cache",
                                    true,
                                    Collections.singletonList(cachedBookings.get())
                            )
                    );
                }
            }

            // Cache miss - fetch from database
            User host = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> {
                        log.error("Host not found with email: {}", emailId);
                        return new UserNotFoundException("User not found with Email id: " + emailId);
                    });
            log.debug("Found host with email: {}", emailId);

            List<Booking> bookings = bookingRepository.findByCarHost(host);

            if (bookings.isEmpty()) {
                log.info("No bookings found for host: {}", emailId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponseData(
                                "No bookings found for this host",
                                false,
                                Collections.emptyList()
                        ));
            }

            List<BookingDto> bookingDtos = bookings.stream()
                    .map(booking -> modelMapper.map(booking, BookingDto.class))
                    .toList();

            // Cache asynchronously
            cacheBookingList(cacheKey, bookingDtos);

            return ResponseEntity.ok(
                    new ApiResponseData(
                            "All bookings fetched successfully",
                            true,
                            Collections.singletonList(bookingDtos)
                    ));

        } catch (UserNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error fetching bookings for host: {}", emailId, e);
            throw new DatabaseException("Error while fetching bookings. Please try again.");
        }
    }

    // Helper methods
    protected double calculateTotalPrice(Car car, LocalDateTime startDate, LocalDateTime endDate) {
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

    private void sendBookingConfirmationEmails(Booking booking, Car car, User renter) {
        CompletableFuture.runAsync(() -> {
            try {
                // Email to renter
                emailService.sendEmail(
                        renter.getEmail(),
                        EmailConstants.bookingConfirmedOfRenterSubject(car.getName()),
                        EmailConstants.bookingConfirmedOfRenterBody
                );

                // Email to host
                emailService.sendEmail(
                        car.getHost().getEmail(),
                        EmailConstants.bookingConfirmedOfSpecificCarOfHostSubject(car.getName(), renter.getFullName()),
                        EmailConstants.bookingConfirmedOfRenterBody
                );

                log.info("Booking confirmation emails sent for booking ID: {}", booking.getBookingId());
            } catch (Exception e) {
                log.error("Failed to send booking confirmation emails for booking ID: {}", booking.getBookingId(), e);
            }
        });
    }

    private void invalidateBookingCaches(Booking booking) {
        CompletableFuture.runAsync(() -> {
            try {
                // Invalidate individual booking cache
                if (redisService != null) {
                    redisService.delete(CACHE_KEY_PREFIX + booking.getBookingId());

                    // Invalidate related caches
                    redisService.delete(CACHE_KEY_PREFIX + "renter:" + booking.getRenter().getEmail());
                    redisService.delete(CACHE_KEY_PREFIX + "car:" + booking.getCar().getCarId());
                    redisService.delete(CACHE_KEY_PREFIX + "host:" + booking.getCar().getHost().getEmail());

                    log.debug("Invalidated caches for booking ID: {}", booking.getBookingId());
                }
            } catch (Exception e) {
                log.error("Failed to invalidate caches for booking ID: {}", booking.getBookingId(), e);
            }
        });
    }

    private void cacheBookingData(String cacheKey, BookingDto bookingDto) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.set(cacheKey, bookingDto, CACHE_TTL);
                    log.debug("Cached booking data for key: {}", cacheKey);
                } catch (Exception e) {
                    log.error("Failed to cache booking data for key: {}", cacheKey, e);
                }
            });
        }
    }

    private <T> void cacheBookingList(String cacheKey, List<T> bookingList) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.setList(cacheKey, bookingList, CACHE_TTL);
                    log.debug("Cached booking list for key: {}", cacheKey);
                } catch (Exception e) {
                    log.error("Failed to cache booking list for key: {}", cacheKey, e);
                }
            });
        }
    }
}