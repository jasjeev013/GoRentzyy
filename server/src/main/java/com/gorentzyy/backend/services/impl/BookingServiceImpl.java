package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.BookingNotFoundException;
import com.gorentzyy.backend.exceptions.CarNotFoundException;
import com.gorentzyy.backend.exceptions.DatabaseException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

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

    @Override
    public ResponseEntity<ApiResponseObject> createBooking(BookingDto bookingDto, Long renterId, Long carId) {
        // Validate car existence
        Car car = carRepository.findById(carId).orElseThrow(() ->
                new CarNotFoundException("Car not found with id: " + carId)
        );

        // Validate renter existence
        User renter = userRepository.findById(renterId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + renterId)
        );

        // Map the BookingDto to the Booking entity
        Booking booking = modelMapper.map(bookingDto, Booking.class);
        booking.setCar(car);
        booking.setRenter(renter);

        LocalDateTime now =  LocalDateTime.now();
        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);

        try {
            // Attempt to save the booking to the database
            Booking savedBooking = bookingRepository.save(booking);

            // Add the booking to car and renter relationships
            car.getBookings().add(booking);
            renter.getBookings().add(booking);


            // Return success response
            return new ResponseEntity<>(new ApiResponseObject(
                    "Booking has been established", true, modelMapper.map(savedBooking, BookingDto.class)
            ), HttpStatus.CREATED);

        } catch (Exception e) {
            // Handle unexpected errors like database issues (if any)
            throw new DatabaseException("Error while saving the booking to the database.");
        }
    }


    @Override
    public ResponseEntity<ApiResponseObject> updateBooking(BookingDto bookingDto, Long bookingId) {
        // Check if user exists by userId, using a more appropriate exception
        Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.")
        );

        existingBooking.setStartDate(bookingDto.getStartDate());
        existingBooking.setEndDate(bookingDto.getEndDate());

        LocalDateTime now = LocalDateTime.now();
        existingBooking.setUpdatedAt(now);

        Booking updatedBooking = bookingRepository.save(existingBooking);

        return new ResponseEntity<>(new ApiResponseObject(
                "Booking updated successfully", true, modelMapper.map(updatedBooking, BookingDto.class)
        ), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ApiResponseObject> getBookingById(Long bookingId) {
        // Check if user exists by userId, using a more appropriate exception
        Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.")
        );

        return new ResponseEntity<>(new ApiResponseObject(
                "Booking fetched successfully", true, modelMapper.map(existingBooking, BookingDto.class)
        ), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ApiResponseObject> cancelBooking(Long bookingId) {


        Booking existingBooking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Booking with ID " + bookingId + " does not exist.")
        );

        bookingRepository.delete(existingBooking);

        return new ResponseEntity<>(new ApiResponseObject(
                "Deleted Successfully", true, null), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByRenter(Long renterId) {
        User renter = userRepository.findById(renterId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + renterId)
        );

        List<Booking> bookings = bookingRepository.findByRenter(renter);
        List<BookingDto> bookingDtos = bookings.stream().map(booking -> modelMapper.map(booking,BookingDto.class)).toList();
        return new ResponseEntity<>(new ApiResponseData(
                "All Bookings fetched",true, Collections.singletonList(bookingDtos)),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByCar(Long carId) {
        // Validate car existence
        Car car = carRepository.findById(carId).orElseThrow(() ->
                new CarNotFoundException("Car not found with id: " + carId)
        );
        List<Booking> bookings = bookingRepository.findByCar(car);
        List<BookingDto> bookingDtos = bookings.stream().map(booking -> modelMapper.map(booking,BookingDto.class)).toList();


        return new ResponseEntity<>(new ApiResponseData(
                "All Bookings fetched",true, Collections.singletonList(bookingDtos)),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseData> getBookingsByHost(Long hostId) {
        // Validate car existence
        User host = userRepository.findById(hostId).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + hostId)
        );
        List<Booking> bookings = bookingRepository.findByCarHost(host);
        List<BookingDto> bookingDtos = bookings.stream().map(booking -> modelMapper.map(booking,BookingDto.class)).toList();


        return new ResponseEntity<>(new ApiResponseData(
                "All Bookings fetched",true, Collections.singletonList(bookingDtos)),HttpStatus.OK);
    }
}
