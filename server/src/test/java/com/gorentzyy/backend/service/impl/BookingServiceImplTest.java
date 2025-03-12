package com.gorentzyy.backend.service.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.BookingDto;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.impl.BookingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Car car;
    private User renter;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setRentalPricePerDay(100.0);
        car.setRentalPricePerWeek(600.0);
        car.setRentalPricePerMonth(2000.0);
        car.setBookings(new ArrayList<>());

        renter = new User();
        renter.setBookings(new ArrayList<>());
        renter.setEmail("renter@example.com");
        renter.setRole(AppConstants.Role.RENTER);

        booking = new Booking();
        booking.setCar(car);
        booking.setRenter(renter);
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(5));
        booking.setStatus(AppConstants.Status.CONFIRMED);

        bookingDto = new BookingDto();
        bookingDto.setStartDate(booking.getStartDate());
        bookingDto.setEndDate(booking.getEndDate());
        bookingDto.setStatus(AppConstants.Status.CONFIRMED);
    }

    @Test
    void testCreateBooking_Success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findByEmail("renter@example.com")).thenReturn(Optional.of(renter));
        when(bookingRepository.findByCarIdAndDateRange(anyLong(), any(), any())).thenReturn(List.of());
        when(modelMapper.map(any(BookingDto.class), eq(Booking.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(any(Booking.class), eq(BookingDto.class))).thenReturn(bookingDto);

        ResponseEntity<ApiResponseObject> response = bookingService.createBooking(bookingDto, "renter@example.com", 1L);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testGetBookingById_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(modelMapper.map(any(Booking.class), eq(BookingDto.class))).thenReturn(bookingDto);

        ResponseEntity<ApiResponseObject> response = bookingService.getBookingById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testCancelBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ResponseEntity<ApiResponseObject> response = bookingService.cancelBooking(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        verify(bookingRepository, times(1)).delete(any(Booking.class));
    }

    @Test
    void testUpdateBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(any(Booking.class), eq(BookingDto.class))).thenReturn(bookingDto);

        ResponseEntity<ApiResponseObject> response = bookingService.updateBooking(bookingDto, 1L);

        assertNotNull(response);
        assertEquals(202, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
}

