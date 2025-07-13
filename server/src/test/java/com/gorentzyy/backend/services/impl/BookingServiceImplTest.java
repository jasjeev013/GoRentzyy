package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.BookingDto;
import com.gorentzyy.backend.payloads.BookingDtoRenter;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.EmailService;
import com.gorentzyy.backend.services.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Mock
    private EmailService emailService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDto bookingDto;
    private Booking booking;
    private Car car;
    private User renter;
    private User host;

    @BeforeEach
    void setUp() {
        // Initialize test data
        bookingDto = new BookingDto();
        bookingDto.setStartDate(LocalDateTime.now().plusDays(1));
        bookingDto.setEndDate(LocalDateTime.now().plusDays(3));
        bookingDto.setStatus(AppConstants.Status.CONFIRMED);


        car = new Car();
        car.setCarId(1L);
        car.setName("Test Car");
        car.setRentalPricePerDay(50.0);
        car.setRentalPricePerWeek(300.0);
        car.setRentalPricePerMonth(1200.0);

        host = new User();
        host.setUserId(1L);
        host.setEmail("host@example.com");
        host.setRole(AppConstants.Role.HOST);
        car.setHost(host);

        renter = new User();
        renter.setUserId(2L);
        renter.setEmail("renter@example.com");
        renter.setRole(AppConstants.Role.RENTER);

        booking = new Booking();

        booking.setCar(car);
        booking.setRenter(renter);
        booking.setStartDate(bookingDto.getStartDate());
        booking.setEndDate(bookingDto.getEndDate());
        booking.setStatus(bookingDto.getStatus());
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setTotalPrice(100.0); // or whatever value you'd expect

    }

    @Test
    @Transactional
    @Retryable(value = {DatabaseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    void createBooking_WhenValidInput_ShouldCreateBookingSuccessfully() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(renter));
        when(bookingRepository.findByCarIdAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        when(modelMapper.map(any(BookingDto.class), eq(Booking.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(any(Booking.class), eq(BookingDto.class))).thenReturn(bookingDto);

        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());



        // Act
        ResponseEntity<ApiResponseObject> response = bookingService.createBooking(
                bookingDto, "renter@example.com", 1L);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Booking has been established", response.getBody().getMessage());

        verify(carRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(bookingRepository, times(1)).findByCarIdAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createBooking_WhenCarNotFound_ShouldThrowCarNotFoundException() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CarNotFoundException.class, () ->
                bookingService.createBooking(bookingDto, "renter@example.com", 1L));
        verify(carRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                bookingService.createBooking(bookingDto, "renter@example.com", 1L));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenUserIsHost_ShouldThrowRoleNotAuthorizedException() {
        // Arrange
        User hostUser = new User();
        hostUser.setRole(AppConstants.Role.HOST);
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(hostUser));

        // Act & Assert
        assertThrows(RoleNotAuthorizedException.class, () ->
                bookingService.createBooking(bookingDto, "host@example.com", 1L));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_WhenCarAlreadyBooked_ShouldThrowBookingConflictException() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(renter));
        when(bookingRepository.findByCarIdAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking()));

        // Act & Assert
        assertThrows(BookingConflictException.class, () ->
                bookingService.createBooking(bookingDto, "renter@example.com", 1L));
        verify(bookingRepository, times(1)).findByCarIdAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_WhenValidInput_ShouldUpdateBookingSuccessfully() {
        // Arrange
        BookingDto updateDto = new BookingDto();
        updateDto.setStatus(AppConstants.Status.CONFIRMED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(booking, BookingDto.class)).thenReturn(updateDto);

        // Act
        ResponseEntity<ApiResponseObject> response = bookingService.updateBooking(updateDto, 1L);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Booking updated successfully", response.getBody().getMessage());
        assertEquals(AppConstants.Status.CONFIRMED, ((BookingDto) response.getBody().getObject()).getStatus());

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getBookingById_WhenBookingExistsInCache_ShouldReturnCachedBooking() {
        // Arrange
        when(redisService.get(anyString(), eq(BookingDto.class))).thenReturn(Optional.of(bookingDto));

        // Act
        ResponseEntity<ApiResponseObject> response = bookingService.getBookingById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Booking found in cache", response.getBody().getMessage());
        assertEquals(bookingDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(BookingDto.class));
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void getBookingById_WhenBookingNotInCacheButExistsInDB_ShouldReturnBookingAndCacheIt() {
        // Arrange
        when(redisService.get(anyString(), eq(BookingDto.class))).thenReturn(Optional.empty());
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(modelMapper.map(booking, BookingDto.class)).thenReturn(bookingDto);
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).set(anyString(), any(BookingDto.class), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseObject> response = bookingService.getBookingById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Booking fetched successfully", response.getBody().getMessage());
        assertEquals(bookingDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(BookingDto.class));
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(redisService, times(1)).set(anyString(), any(BookingDto.class), any(Duration.class));
    }

    @Test
    void cancelBooking_WhenValidInput_ShouldCancelBookingSuccessfully() {
        // Arrange
        booking.setStatus(AppConstants.Status.PENDING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        doNothing().when(bookingRepository).delete(any(Booking.class));

        // Act
        ResponseEntity<ApiResponseObject> response = bookingService.cancelBooking(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Booking cancelled successfully", response.getBody().getMessage());

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).delete(any(Booking.class));
    }

    @Test
    void getBookingsByRenter_WhenRenterExists_ShouldReturnBookings() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(renter));
        when(bookingRepository.findByRenter(any(User.class))).thenReturn(List.of(booking));
        when(modelMapper.map(booking, BookingDtoRenter.class)).thenReturn(new BookingDtoRenter());
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).setList(anyString(), anyList(), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseData> response = bookingService.getBookingsByRenter("renter@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("All bookings fetched successfully", response.getBody().getMessage());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(bookingRepository, times(1)).findByRenter(any(User.class));
    }

    @Test
    void getBookingsByCar_WhenCarExists_ShouldReturnBookings() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(bookingRepository.findByCar(any(Car.class))).thenReturn(List.of(booking));
        when(modelMapper.map(booking, BookingDto.class)).thenReturn(bookingDto);
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).setList(anyString(), anyList(), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseData> response = bookingService.getBookingsByCar(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("All bookings fetched successfully", response.getBody().getMessage());

        verify(carRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByCar(any(Car.class));
    }

    @Test
    void getBookingsByHost_WhenHostExists_ShouldReturnBookings() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(host));
        when(bookingRepository.findByCarHost(any(User.class))).thenReturn(List.of(booking));
        when(modelMapper.map(booking, BookingDto.class)).thenReturn(bookingDto);
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).setList(anyString(), anyList(), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseData> response = bookingService.getBookingsByHost("host@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("All bookings fetched successfully", response.getBody().getMessage());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(bookingRepository, times(1)).findByCarHost(any(User.class));
    }

    @Test
    void calculateTotalPrice_WhenBetween7And30Days_ShouldCalculateWeeklyPrice() {
        // Setup
        Car testCar = new Car();
        testCar.setRentalPricePerDay(50.0);
        testCar.setRentalPricePerWeek(300.0);
        testCar.setRentalPricePerMonth(1200.0);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(10); // 1 week + 3 days

        // Act
        double totalPrice = bookingService.calculateTotalPrice(testCar, startDate, endDate);

        // Assert
        assertEquals(450.0, totalPrice); // 1 week (300) + 3 days (150)
    }

    @Test
    void calculateTotalPrice_WhenMoreThan30Days_ShouldCalculateMonthlyPrice() {
        // Setup
        Car testCar = new Car();
        testCar.setRentalPricePerDay(50.0);
        testCar.setRentalPricePerWeek(300.0);
        testCar.setRentalPricePerMonth(1200.0);

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(40); // 1 month + 10 days

        // Act
        double totalPrice = bookingService.calculateTotalPrice(testCar, startDate, endDate);

        // Assert
        assertEquals(1700.0, totalPrice); // 1 month (1200) + 1 week (300) + 3 days (150) + 1 day (50)
    }
}