package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.CarNotFoundException;
import com.gorentzyy.backend.exceptions.LocationNotFoundException;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Location;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.LocationDto;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    private LocationDto locationDto;
    private Location location;
    private Car car;
    private User hostUser;

    @BeforeEach
    void setUp() {
        // Initialize test data
        locationDto = new LocationDto();
        locationDto.setLocationId(1L);
        locationDto.setCity("Test City");
        locationDto.setAddress("123 Test St");
        locationDto.setLatitude(12.34);
        locationDto.setLongitude(56.78);

        location = new Location();
        location.setLocationId(1L);
        location.setCity("Test City");
        location.setAddress("123 Test St");
        location.setLatitude(12.34);
        location.setLongitude(56.78);

        hostUser = new User();
        hostUser.setUserId(1L);
        hostUser.setEmail("host@example.com");
        hostUser.setRole(AppConstants.Role.HOST);

        car = new Car();
        car.setCarId(1L);
        car.setHost(hostUser);
    }

    @Test
    void addLocation_WhenValidInput_ShouldCreateLocationSuccessfully() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(modelMapper.map(locationDto, Location.class)).thenReturn(location);
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        when(modelMapper.map(location, LocationDto.class)).thenReturn(locationDto);

        // Act
        ResponseEntity<ApiResponseObject> response = locationService.addLocation(
                locationDto, 1L, "host@example.com");

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Location has been created", response.getBody().getMessage());
        assertEquals(locationDto, response.getBody().getObject());

        verify(carRepository, times(1)).findById(anyLong());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void addLocation_WhenCarNotFound_ShouldThrowCarNotFoundException() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CarNotFoundException.class, () ->
                locationService.addLocation(locationDto, 1L, "host@example.com"));
        verify(carRepository, times(1)).findById(anyLong());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void addLocation_WhenNotOwner_ShouldThrowRuntimeException() {
        // Arrange
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                locationService.addLocation(locationDto, 1L, "notowner@example.com"));
        assertEquals("Owner of this car can only set Location", exception.getMessage());
        verify(carRepository, times(1)).findById(anyLong());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void updateLocation_WhenValidInput_ShouldUpdateLocationSuccessfully() {
        // Arrange
        LocationDto updateDto = new LocationDto();
        updateDto.setCity("Updated City");
        updateDto.setAddress("456 Updated St");
        updateDto.setLatitude(23.45);
        updateDto.setLongitude(67.89);

        location.setCar(car); // Set the car for the location
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        when(modelMapper.map(location, LocationDto.class)).thenReturn(updateDto);

        // Act
        ResponseEntity<ApiResponseObject> response = locationService.updateLocation(
                updateDto, 1L, "host@example.com");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Location is updated", response.getBody().getMessage());
        assertEquals(updateDto, response.getBody().getObject());

        verify(locationRepository, times(1)).findById(anyLong());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void updateLocation_WhenLocationNotFound_ShouldThrowLocationNotFoundException() {
        // Arrange
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LocationNotFoundException.class, () ->
                locationService.updateLocation(locationDto, 1L, "host@example.com"));
        verify(locationRepository, times(1)).findById(anyLong());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void updateLocation_WhenNotOwner_ShouldThrowRuntimeException() {
        // Arrange
        location.setCar(car); // Set the car for the location
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(location));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                locationService.updateLocation(locationDto, 1L, "notowner@example.com"));
        assertEquals("Owner of this car can only update Location", exception.getMessage());
        verify(locationRepository, times(1)).findById(anyLong());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void getLocation_WhenLocationExists_ShouldReturnLocation() {
        // Arrange
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(location));
        when(modelMapper.map(location, LocationDto.class)).thenReturn(locationDto);

        // Act
        ResponseEntity<ApiResponseObject> response = locationService.getLocation(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Location is retrieved", response.getBody().getMessage());
        assertEquals(locationDto, response.getBody().getObject());

        verify(locationRepository, times(1)).findById(anyLong());
    }

    @Test
    void getLocation_WhenLocationNotFound_ShouldThrowLocationNotFoundException() {
        // Arrange
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LocationNotFoundException.class, () ->
                locationService.getLocation(1L));
        verify(locationRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteLocation_WhenLocationExists_ShouldDeleteLocationSuccessfully() {
        // Arrange
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(location));
        doNothing().when(locationRepository).delete(any(Location.class));

        // Act
        ResponseEntity<ApiResponseObject> response = locationService.deleteLocation(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The location is deleted successfully", response.getBody().getMessage());

        verify(locationRepository, times(1)).findById(anyLong());
        verify(locationRepository, times(1)).delete(any(Location.class));
    }

    @Test
    void deleteLocation_WhenLocationNotFound_ShouldThrowLocationNotFoundException() {
        // Arrange
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LocationNotFoundException.class, () ->
                locationService.deleteLocation(1L));
        verify(locationRepository, times(1)).findById(anyLong());
        verify(locationRepository, never()).delete(any(Location.class));
    }
}