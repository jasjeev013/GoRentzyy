package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.CarNotFoundException;
import com.gorentzyy.backend.exceptions.DatabaseException;
import com.gorentzyy.backend.exceptions.LocationNotFoundException;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Location;
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
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setLocation(location);

        locationDto = new LocationDto();
        locationDto.setCity("Test Location");
        locationDto.setAddress("123 Test Street");
        locationDto.setLatitude(40.7128);
        locationDto.setLongitude(-74.0060);

        location = new Location();
        location.setCity("Test Location");
        location.setAddress("123 Test Street");
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);
        location.setCar(car);

    }

    @Test
    void testAddLocation_Success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(modelMapper.map(locationDto, Location.class)).thenReturn(location);
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        when(modelMapper.map(location, LocationDto.class)).thenReturn(locationDto);

        ResponseEntity<ApiResponseObject> response = locationService.addLocation(locationDto, 1L);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("The Location has been created", response.getBody().getMessage());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void testAddLocation_CarNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> locationService.addLocation(locationDto, 1L));

        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void testUpdateLocation_Success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        when(modelMapper.map(location, LocationDto.class)).thenReturn(locationDto);

        ResponseEntity<ApiResponseObject> response = locationService.updateLocation(locationDto, 1L);

        assertEquals(202, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("The Location is updated", response.getBody().getMessage());
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void testUpdateLocation_NotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LocationNotFoundException.class, () -> locationService.updateLocation(locationDto, 1L));

        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void testGetLocation_Success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(modelMapper.map(location, LocationDto.class)).thenReturn(locationDto);

        ResponseEntity<ApiResponseObject> response = locationService.getLocation(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("The Location is retrieved", response.getBody().getMessage());
        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetLocation_NotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LocationNotFoundException.class, () -> locationService.getLocation(1L));

        verify(locationRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteLocation_Success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        doNothing().when(locationRepository).delete(location);

        ResponseEntity<ApiResponseObject> response = locationService.deleteLocation(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("The location is deleted successfully", response.getBody().getMessage());
        verify(locationRepository, times(1)).delete(location);
    }

    @Test
    void testDeleteLocation_NotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LocationNotFoundException.class, () -> locationService.deleteLocation(1L));

        verify(locationRepository, never()).delete(any(Location.class));
    }

    @Test
    void testDeleteLocation_DatabaseError() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        doThrow(new RuntimeException("DB error")).when(locationRepository).delete(location);

        assertThrows(DatabaseException.class, () -> locationService.deleteLocation(1L));

        verify(locationRepository, times(1)).delete(location);
    }
}
