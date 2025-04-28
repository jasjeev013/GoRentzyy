package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.CarAlreadyExistsException;
import com.gorentzyy.backend.exceptions.CarNotFoundException;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.UserRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CarServiceImpl carService;

    private CarDto carDto;
    private Car car;
    private User host;

    @BeforeEach
    void setUp() {
        carDto = new CarDto();
        carDto.setMake("Toyota");
        carDto.setModel("Corolla");
        carDto.setYear(2020);
        carDto.setColor("Blue");
        carDto.setRegistrationNumber("ABC123");
        carDto.setCarCategory(AppConstants.CarCategory.SUV);
        carDto.setCarType(AppConstants.CarType.ECONOMY);
        carDto.setFuelType(AppConstants.FuelType.PETROL);
        carDto.setSeatingCapacity(5);
        carDto.setRentalPricePerDay(50);
        carDto.setRentalPricePerWeek(300);
        carDto.setRentalPricePerMonth(1000);
        carDto.setAvailabilityStatus(AppConstants.AvailabilityStatus.AVAILABLE);
        carDto.setMaintenanceDueDate(LocalDateTime.now().plusDays(30));

        host = new User();

        host.setEmail("host@example.com");
        host.setRole(AppConstants.Role.HOST);
        host.setCars(new ArrayList<>());

        car = new Car();
        car.setMake("Toyota");
        car.setModel("Corolla");
        car.setYear(2020);
        car.setColor("Blue");
        car.setRegistrationNumber("ABC123");
        carDto.setCarCategory(AppConstants.CarCategory.SUV);
        carDto.setCarType(AppConstants.CarType.ECONOMY);
        car.setFuelType(AppConstants.FuelType.PETROL);
        car.setSeatingCapacity(5);
        car.setRentalPricePerDay(50);
        car.setRentalPricePerWeek(300);
        car.setRentalPricePerMonth(1000);
        car.setAvailabilityStatus(AppConstants.AvailabilityStatus.AVAILABLE);
        car.setMaintenanceDueDate(LocalDateTime.now().plusDays(30));
        car.setHost(host);
    }

    @Test
    void testAddNewCar_Success() {
        when(carRepository.existsByRegistrationNumber(carDto.getRegistrationNumber())).thenReturn(false);
        when(userRepository.findByEmail(host.getEmail())).thenReturn(Optional.of(host));
        when(modelMapper.map(carDto, Car.class)).thenReturn(car);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(modelMapper.map(car, CarDto.class)).thenReturn(carDto);

        ResponseEntity<ApiResponseObject> response = carService.addNewCar(carDto, host.getEmail());

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testAddNewCar_CarAlreadyExists() {
        when(carRepository.existsByRegistrationNumber(carDto.getRegistrationNumber())).thenReturn(true);

        assertThrows(CarAlreadyExistsException.class, () -> carService.addNewCar(carDto, host.getEmail()));
    }

    @Test
    void testGetCarById_Success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(modelMapper.map(car, CarDto.class)).thenReturn(carDto);

        ResponseEntity<ApiResponseObject> response = carService.getCarById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isResult());
    }

    @Test
    void testGetCarById_CarNotFound() {
        when(carRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.getCarById(2L));
    }

    @Test
    void testRemoveCar_Success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        doNothing().when(carRepository).delete(car);

        ResponseEntity<ApiResponseObject> response = carService.removeCar(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        verify(carRepository, times(1)).delete(car);
    }

    @Test
    void testRemoveCar_CarNotFound() {
        when(carRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.removeCar(2L));
    }
}

