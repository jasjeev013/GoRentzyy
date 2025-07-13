package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Location;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.LocationDto;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.LocationRepository;
import com.gorentzyy.backend.services.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {

  

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(CarRepository carRepository,
                               ModelMapper modelMapper,
                               LocationRepository locationRepository) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.locationRepository = locationRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> addLocation(LocationDto locationDto, Long carId, String email) {
        log.info("Adding location for car ID: {} by user: {}", carId, email);

        try {
            validateLocationDto(locationDto);

            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> {
                        log.error("Car not found with ID: {}", carId);
                        return new CarNotFoundException("Car with ID " + carId + " does not exist.");
                    });

            validateCarOwnership(car, email, "add location");

            if (car.getLocation() != null) {
                log.warn("Car ID: {} already has a location", carId);
                throw new LocationAlreadyExistsException("Car already has a location assigned");
            }

            Location newLocation = modelMapper.map(locationDto, Location.class);
            newLocation.setCar(car);
            car.setLocation(newLocation);

            Location savedLocation = locationRepository.save(newLocation);
            log.info("Successfully added location ID: {} for car ID: {}", savedLocation.getLocationId(), carId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseObject(
                            "Location created successfully",
                            true,
                            modelMapper.map(savedLocation, LocationDto.class)));


        } catch (CarNotFoundException | LocationAlreadyExistsException |
                 InvalidLocationDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding location for car ID: {}", carId, e);
            throw new DatabaseException("Error while saving the location");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updateLocation(LocationDto locationDto, Long locationId, String email) {
        log.info("Updating location ID: {} by user: {}", locationId, email);

        try {
            validateLocationDto(locationDto);

            Location existingLocation = locationRepository.findById(locationId)
                    .orElseThrow(() -> {
                        log.error("Location not found with ID: {}", locationId);
                        return new LocationNotFoundException("Location with ID " + locationId + " does not exist.");
                    });

            validateCarOwnership(existingLocation.getCar(), email, "update location");

            updateLocationFields(existingLocation, locationDto);

            Location updatedLocation = locationRepository.save(existingLocation);
            log.info("Successfully updated location ID: {}", locationId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseObject(
                            "Location updated successfully",
                            true,
                            modelMapper.map(updatedLocation, LocationDto.class)
                    ));

        } catch (LocationNotFoundException | InvalidLocationDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating location ID: {}", locationId, e);
            throw new DatabaseException("Error while updating the location");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getLocation(Long locationId) {
        log.info("Fetching location with ID: {}", locationId);

        try {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> {
                        log.error("Location not found with ID: {}", locationId);
                        return new LocationNotFoundException("Location with ID " + locationId + " does not exist.");
                    });

            log.info("Successfully retrieved location ID: {}", locationId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Location retrieved successfully",
                    true,
                    modelMapper.map(location, LocationDto.class))
            );

        } catch (LocationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching location ID: {}", locationId, e);
            throw new DatabaseException("Error while retrieving the location");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> deleteLocation(Long locationId) {
        log.info("Deleting location with ID: {}", locationId);

        try {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> {
                        log.error("Location not found with ID: {}", locationId);
                        return new LocationNotFoundException("Location with ID " + locationId + " does not exist.");
                    });

            locationRepository.delete(location);
            log.info("Successfully deleted location ID: {}", locationId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Location deleted successfully",
                    true,
                    null)
            );

        } catch (LocationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting location ID: {}", locationId, e);
            throw new DatabaseException("Error while deleting the location");
        }
    }

    private void validateLocationDto(LocationDto locationDto) {
        if (locationDto == null) {
            throw new InvalidLocationDataException("Location data cannot be null");
        }
        if (!StringUtils.hasText(locationDto.getAddress())) {
            throw new InvalidLocationDataException("Address cannot be empty");
        }
        if (!StringUtils.hasText(locationDto.getCity())) {
            throw new InvalidLocationDataException("City cannot be empty");
        }
    }

    private void validateCarOwnership(Car car, String email, String operation) {
        if (!Objects.equals(car.getHost().getEmail(), email)) {
            log.warn("Unauthorized {} attempt by {} for car ID: {}", operation, email, car.getCarId());
            throw new UnauthorizedAccessException("Only the car owner can " + operation);
        }
    }

    private void updateLocationFields(Location location, LocationDto locationDto) {
        if (StringUtils.hasText(locationDto.getAddress())) {
            location.setAddress(locationDto.getAddress());
        }
        if (StringUtils.hasText(locationDto.getCity())) {
            location.setCity(locationDto.getCity());
        }

    }
}