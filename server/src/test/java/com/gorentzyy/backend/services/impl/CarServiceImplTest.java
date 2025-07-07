package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.CarAlreadyExistsException;
import com.gorentzyy.backend.exceptions.CarNotFoundException;
import com.gorentzyy.backend.exceptions.RoleNotAuthorizedException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Location;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.*;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.LocationRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.CloudinaryService;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private CarServiceImpl carService;

    private CarDto carDto;
    private Car car;
    private LocationDto locationDto;
    private User hostUser;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Setup test data
        carDto = new CarDto();
        carDto.setCarId(1L);
        carDto.setName("Test Car");
        carDto.setMake("Toyota");
        carDto.setModel("Camry");
        carDto.setYear(2022);
        carDto.setRegistrationNumber("ABC123");
        carDto.setCarCategory(AppConstants.CarCategory.SEDAN);
        carDto.setCarType(AppConstants.CarType.ELECTRIC);
        carDto.setFuelType(AppConstants.FuelType.PETROL);
        carDto.setTransmissionMode(AppConstants.TransmissionMode.AUTOMATIC);
        carDto.setSeatingCapacity(5);
        carDto.setLuggageCapacity(2);
        carDto.setRentalPricePerDay(50.0);
        carDto.setAvailabilityStatus(AppConstants.AvailabilityStatus.AVAILABLE);

        locationDto = new LocationDto();
        locationDto.setAddress("123 Test St");
        locationDto.setCity("Test City");
        locationDto.setLatitude(12.34);
        locationDto.setLongitude(56.78);

        hostUser = new User();
        hostUser.setUserId(1L);
        hostUser.setEmail("host@example.com");
        hostUser.setRole(AppConstants.Role.HOST);
        hostUser.setCars(new ArrayList<>());

        car = new Car();
        car.setCarId(1L);
        car.setHost(hostUser);
        car.setName("Test Car");
        car.setRegistrationNumber("ABC123");
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());

        // Initialize car without setting host (or use a mock)
//
        mockFile = mock(MultipartFile.class);
//        when(mockFile.getContentType()).thenReturn("image/jpeg");
    }

    @Test
    void addNewCar_WhenValidInput_ShouldCreateCarSuccessfully() {
        // Arrange
        when(carRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(hostUser));
        when(modelMapper.map(carDto, Car.class)).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(modelMapper.map(car, CarDto.class)).thenReturn(carDto);
        when(locationRepository.save(any(Location.class))).thenReturn(new Location());
        when(modelMapper.map(locationDto, Location.class)).thenReturn(new Location());
        when(cloudinaryService.upload(any(MultipartFile.class)))
                .thenReturn(Map.of("url", "https://test.com/photo.jpg"));

        // Act
        ResponseEntity<ApiResponseObject> response = carService.addNewCar(
                carDto, "host@example.com", List.of(mockFile), locationDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The car was added successfully", response.getBody().getMessage());

        // Verify the car was added to the host's cars list
        assertTrue(hostUser.getCars().contains(car));

        verify(carRepository, times(1)).existsByRegistrationNumber(anyString());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(carRepository, times(2)).save(any(Car.class));
        verify(cloudinaryService, atLeastOnce()).upload(any(MultipartFile.class));
    }

    @Test
    void addNewCar_WhenCarExists_ShouldThrowCarAlreadyExistsException() {
        // Arrange
        when(carRepository.existsByRegistrationNumber(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(CarAlreadyExistsException.class, () ->
                carService.addNewCar(carDto, "host@example.com", null, null));
        verify(carRepository, times(1)).existsByRegistrationNumber(anyString());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void addNewCar_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(carRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                carService.addNewCar(carDto, "host@example.com", null, null));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void addNewCar_WhenUserNotHost_ShouldThrowRoleNotAuthorizedException() {
        // Arrange
        User renterUser = new User();
        renterUser.setRole(AppConstants.Role.RENTER);
        when(carRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(renterUser));

        // Act & Assert
        assertThrows(RoleNotAuthorizedException.class, () ->
                carService.addNewCar(carDto, "renter@example.com", null, null));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_WhenValidInput_ShouldUpdateCarSuccessfully() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(modelMapper.map(car, CarDto.class)).thenReturn(carDto);
        when(modelMapper.map(locationDto, Location.class)).thenReturn(new Location());


        // Act
        ResponseEntity<ApiResponseObject> response = carService.updateCar(
                carDto, 1L, List.of(mockFile), locationDto, "host@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Car updated successfully", response.getBody().getMessage());

        verify(carRepository, times(1)).findById(anyLong());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void updateCar_WhenCarNotFound_ShouldThrowCarNotFoundException() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CarNotFoundException.class, () ->
                carService.updateCar(carDto, 1L, null, null, "host@example.com"));
        verify(carRepository, times(1)).findById(anyLong());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCar_WhenNotOwner_ShouldThrowRoleNotAuthorizedException() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        // Act & Assert
        assertThrows(RoleNotAuthorizedException.class, () ->
                carService.updateCar(carDto, 1L, null, null, "notowner@example.com"));
        verify(carRepository, times(1)).findById(anyLong());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void getCarById_WhenCarExistsInCache_ShouldReturnCachedCar() {
        // Arrange
        when(redisService.get(anyString(), eq(CarDto.class))).thenReturn(Optional.of(carDto));

        // Act
        ResponseEntity<ApiResponseObject> response = carService.getCarById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("User found in cache", response.getBody().getMessage());
        assertEquals(carDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(CarDto.class));
        verify(carRepository, never()).findById(anyLong());
    }

    @Test
    void getCarById_WhenCarNotInCacheButExistsInDB_ShouldReturnCarAndCacheIt() {
        // Arrange
        when(redisService.get(anyString(), eq(CarDto.class))).thenReturn(Optional.empty());
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(modelMapper.map(car, CarDto.class)).thenReturn(carDto);
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).set(anyString(), any(CarDto.class), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseObject> response = carService.getCarById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The car is found", response.getBody().getMessage());
        assertEquals(carDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(CarDto.class));
        verify(carRepository, times(1)).findById(anyLong());
        verify(redisService, times(1)).set(anyString(), any(CarDto.class), any(Duration.class));
    }

    @Test
    void removeCar_WhenValidInput_ShouldDeleteCarSuccessfully() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        doNothing().when(carRepository).delete(any(Car.class));

        // Act
        ResponseEntity<ApiResponseObject> response = carService.removeCar(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Deleted Successfully", response.getBody().getMessage());

        verify(carRepository, times(1)).findById(anyLong());
        verify(carRepository, times(1)).delete(any(Car.class));
    }

    @Test
    void getAllCarsForSpecificHost_WhenValidHost_ShouldReturnCars() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(hostUser));
        when(carRepository.findCarsByHostEmailWithDetails(anyString())).thenReturn(List.of(car));
        when(modelMapper.map(car, CarDtoHost.class)).thenReturn(new CarDtoHost());
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).setList(anyString(), anyList(), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseData> response = carService.getAllCarsForSpecificHost("host@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("All cars for the specific host found", response.getBody().getMessage());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(carRepository, times(1)).findCarsByHostEmailWithDetails(anyString());
    }

    @Test
    void addCarPhotos_WhenValidInput_ShouldAddPhotosSuccessfully() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(cloudinaryService.upload(any(MultipartFile.class))).thenReturn(Map.of("url", "https://test.com/photo.jpg"));

        // Act
        ResponseEntity<ApiResponseObject> response = carService.addCarPhotos(List.of(mockFile), 1L);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Uploaded all photos", response.getBody().getMessage());

        verify(carRepository, times(1)).findById(anyLong());
        verify(cloudinaryService, times(1)).upload(any(MultipartFile.class));
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void getAllCars_ShouldReturnAllCars() {
        // Arrange
        when(carRepository.findAll()).thenReturn(List.of(car));
        when(modelMapper.map(car, CarDto.class)).thenReturn(carDto);

        // Act
        ResponseEntity<ApiResponseData> response = carService.getAllCars();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("All cars found", response.getBody().getMessage());

        verify(carRepository, times(1)).findAll();
    }
}