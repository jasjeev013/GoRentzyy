package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Location;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.*;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.LocationRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.CarService;
import com.gorentzyy.backend.services.CloudinaryService;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CarServiceImpl implements CarService {
    
    private static final String CACHE_KEY_PREFIX = "car:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final LocationRepository locationRepository;
    private final RedisService redisService;

    @Autowired
    public CarServiceImpl(UserRepository userRepository, CarRepository carRepository,
                          ModelMapper modelMapper, CloudinaryService cloudinaryService,
                          LocationRepository locationRepository, RedisService redisService) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
        this.locationRepository = locationRepository;
        this.redisService = redisService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Retryable(value = {DatabaseException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public ResponseEntity<ApiResponseObject> addNewCar(CarDto carDto, String email, List<MultipartFile> files, LocationDto locationDto) {
        log.info("Adding new car with registration: {} by host: {}", carDto.getRegistrationNumber(), email);

        try {
            // Validate car doesn't already exist
            if (carRepository.existsByRegistrationNumber(carDto.getRegistrationNumber())) {
                log.warn("Car with registration {} already exists", carDto.getRegistrationNumber());
                throw new CarAlreadyExistsException("A car with this registration already exists.");
            }

            // Validate host exists and is authorized
            User host = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("Host not found with email: {}", email);
                        return new UserNotFoundException("User with ID " + email + " does not exist.");
                    });

            if (host.getRole() == AppConstants.Role.RENTER) {
                log.error("Unauthorized attempt to add car by renter: {}", email);
                throw new RoleNotAuthorizedException("Role Not Authorized to add cars");
            }

            // Create new car
            Car newCar = modelMapper.map(carDto, Car.class);
            newCar.setHost(host);
            host.getCars().add(newCar);

            // Handle location
            if (locationDto != null) {
                Location location = modelMapper.map(locationDto, Location.class);
                location.setCar(newCar);
                newCar.setLocation(location);
                locationRepository.save(location);
                log.debug("Added location for new car");
            }

            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            newCar.setCreatedAt(now);
            newCar.setUpdatedAt(now);

            // Save car to get ID
            Car savedCar = carRepository.save(newCar);
            log.debug("Initial car saved with ID: {}", savedCar.getCarId());

            // Handle photo uploads
            if (files != null && !files.isEmpty()) {
                List<String> photoUrls = uploadPhotos(files, savedCar.getCarId());
                savedCar.setPhotos(photoUrls);
                carRepository.save(savedCar);
                log.info("Uploaded {} photos for car ID: {}", photoUrls.size(), savedCar.getCarId());
            }

            // Prepare response
            CarResponseDto responseDto = buildCarResponseDto(savedCar);

            // Invalidate host's car cache
            invalidateHostCarCache(email);

            log.info("Successfully added car ID: {} with registration: {}",
                    savedCar.getCarId(), savedCar.getRegistrationNumber());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseObject(
                            "The car was added successfully",
                            true,
                            responseDto
                    ));

        } catch (CarAlreadyExistsException | UserNotFoundException |
                 RoleNotAuthorizedException | InvalidCarDataException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            log.error("Unexpected error adding new car", e);
            throw new DatabaseException("Error while saving the car to the database.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updateCar(CarDto carDto, Long carId, List<MultipartFile> files, LocationDto locationDto, String email) {
        log.info("Updating car ID: {} by host: {}", carId, email);

        try {
            // Get existing car
            Car existingCar = carRepository.findById(carId)
                    .orElseThrow(() -> {
                        log.error("Car not found with ID: {}", carId);
                        return new CarNotFoundException("Car with ID " + carId + " does not exist.");
                    });

            // Verify ownership
            if (!existingCar.getHost().getEmail().equals(email)) {
                log.error("Unauthorized update attempt by: {} for car ID: {}", email, carId);
                throw new RoleNotAuthorizedException("You are not authorized to update this car");
            }

            // Update car details
            updateCarFields(existingCar, carDto);
            existingCar.setUpdatedAt(LocalDateTime.now());

            // Handle location update
            updateCarLocation(existingCar, locationDto);

            // Handle photo uploads
            if (files != null && !files.isEmpty()) {
                List<String> newPhotoUrls = uploadPhotos(files, carId);
                List<String> existingPhotos = existingCar.getPhotos() != null ?
                        new ArrayList<>(existingCar.getPhotos()) : new ArrayList<>();
                existingPhotos.addAll(newPhotoUrls);
                existingCar.setPhotos(existingPhotos);
                log.info("Added {} new photos to car ID: {}", newPhotoUrls.size(), carId);
            }

            // Save updated car
            Car updatedCar = carRepository.save(existingCar);

            // Prepare response
            CarResponseDto responseDto = buildCarResponseDto(updatedCar);

            // Invalidate caches
            invalidateCarCaches(updatedCar);

            log.info("Successfully updated car ID: {}", carId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Car updated successfully",
                    true,
                    responseDto
            ));

        } catch (CarNotFoundException | RoleNotAuthorizedException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            log.error("Unexpected error updating car ID: {}", carId, e);
            throw new DatabaseException("Error while updating the car in the database.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getCarById(Long carId) {
        log.info("Fetching car with ID: {}", carId);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + carId;
            if (redisService != null) {
                Optional<CarDto> cachedCar = redisService.get(cacheKey, CarDto.class);
                if (cachedCar.isPresent()) {
                    log.debug("Cache hit for car ID: {}", carId);
                    return ResponseEntity.ok(
                            new ApiResponseObject("Car found in cache", true, cachedCar.get())
                    );
                }
            }

            // Cache miss - fetch from database
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> {
                        log.error("Car not found with ID: {}", carId);
                        return new CarNotFoundException("Car with ID " + carId + " does not exist.");
                    });

            CarDto carDto = modelMapper.map(car, CarDto.class);

            // Cache asynchronously
            cacheCarData(cacheKey, carDto);

            log.info("Successfully fetched car ID: {}", carId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "The car is found",
                    true,
                    carDto
            ));

        } catch (CarNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error fetching car ID: {}", carId, e);
            throw new DatabaseException("An error occurred while retrieving the car.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> removeCar(Long carId) {
        log.info("Deleting car with ID: {}", carId);

        try {
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> {
                        log.error("Car not found with ID: {}", carId);
                        return new CarNotFoundException("Car with ID " + carId + " does not exist.");
                    });

            // Invalidate caches before deletion
            invalidateCarCaches(car);

            carRepository.delete(car);
            log.info("Successfully deleted car ID: {}", carId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Deleted Successfully",
                    true,
                    null
            ));

        } catch (CarNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error deleting car ID: {}", carId, e);
            throw new DatabaseException("An error occurred while deleting the car.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificHost(String email) {
        log.info("Fetching all cars for host: {}", email);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + "host:" + email;
            if (redisService != null) {
                Optional<List<CarDtoHost>> cachedCars = redisService.getList(cacheKey, CarDtoHost.class);
                if (cachedCars.isPresent() && !cachedCars.get().isEmpty()) {
                    log.debug("Cache hit for host cars: {}", email);
                    return ResponseEntity.ok(new ApiResponseData(
                            "Cars found in cache",
                            true,
                            Collections.singletonList(cachedCars.get())
                    ));
                }
            }

            // Validate host exists and is authorized
            User host = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("Host not found with email: {}", email);
                        return new UserNotFoundException("User with ID " + email + " does not exist.");
                    });

            if (host.getRole() == AppConstants.Role.RENTER) {
                log.error("Unauthorized access attempt by renter: {}", email);
                throw new RoleNotAuthorizedException("User with ID " + email + " is not authorized.");
            }

            // Fetch cars
            List<Car> cars = carRepository.findCarsByHostEmailWithDetails(email);

            if (cars.isEmpty()) {
                log.info("No cars found for host: {}", email);
                return ResponseEntity.noContent().build();
            }

            List<CarDtoHost> carDtos = cars.stream()
                    .map(car -> {
                        car.setPhotos(new ArrayList<>(car.getPhotos())); // Force unwrap photos
                        return modelMapper.map(car, CarDtoHost.class);
                    })
                    .toList();

            // Cache asynchronously
            cacheCarList(cacheKey, carDtos);

            log.info("Fetched {} cars for host: {}", carDtos.size(), email);

            return ResponseEntity.ok(new ApiResponseData(
                    "All cars for the specific host found",
                    true,
                    Collections.singletonList(carDtos)
            ));

        } catch (UserNotFoundException | RoleNotAuthorizedException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            log.error("Unexpected error fetching cars for host: {}", email, e);
            throw new DatabaseException("Error while fetching cars for the host. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCars() {
        log.info("Fetching all cars");

        try {
            List<Car> cars = carRepository.findAll();
            List<CarDto> carDtos = cars.stream()
                    .map(car -> modelMapper.map(car, CarDto.class))
                    .toList();

            log.info("Fetched {} cars", carDtos.size());

            return ResponseEntity.ok(new ApiResponseData(
                    "All cars found",
                    true,
                    Collections.singletonList(carDtos)
            ));
        } catch (Exception e) {
            log.error("Unexpected error fetching all cars", e);
            throw new DatabaseException("Error while fetching all cars. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificCity(String city) {
        log.info("Fetching cars in city: {}", city);

        try {
            List<Car> cars = carRepository.findCarsByLocation_City(city);
            List<CarDto> carDtos = cars.stream()
                    .map(car -> modelMapper.map(car, CarDto.class))
                    .toList();

            log.info("Fetched {} cars in city: {}", carDtos.size(), city);

            return ResponseEntity.ok(new ApiResponseData(
                    "All cars found",
                    true,
                    Collections.singletonList(carDtos)
            ));
        } catch (Exception e) {
            log.error("Unexpected error fetching cars in city: {}", city, e);
            throw new DatabaseException("Error while fetching cars by city. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForMakeAndModel(String make, String model) {
        log.info("Fetching cars with make: {} and model: {}", make, model);

        try {
            List<Car> cars = carRepository.findCarsByMakeAndModel(make, model);
            List<CarDto> carDtos = cars.stream()
                    .map(car -> modelMapper.map(car, CarDto.class))
                    .toList();

            log.info("Fetched {} cars with make {} and model {}", carDtos.size(), make, model);

            return ResponseEntity.ok(new ApiResponseData(
                    "All cars found",
                    true,
                    Collections.singletonList(carDtos)
            ));
        } catch (Exception e) {
            log.error("Unexpected error fetching cars with make {} and model {}", make, model, e);
            throw new DatabaseException("Error while fetching cars by make and model. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificCityWithNotHavingStartDateANdEndDate(
            String city, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching available cars in city: {} between {} and {}", city, startDate, endDate);

        try {
            List<Car> cars = carRepository.findAvailableCarsInCityBetweenDates(city, startDate, endDate);
            List<CarDto> carDtos = cars.stream()
                    .map(car -> modelMapper.map(car, CarDto.class))
                    .toList();

            log.info("Fetched {} available cars in city {} between {} and {}",
                    carDtos.size(), city, startDate, endDate);

            return ResponseEntity.ok(new ApiResponseData(
                    "All cars found",
                    true,
                    Collections.singletonList(carDtos)
            ));
        } catch (Exception e) {
            log.error("Unexpected error fetching available cars in city {} between {} and {}",
                    city, startDate, endDate, e);
            throw new DatabaseException("Error while fetching available cars. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> addCarPhotos(List<MultipartFile> files, Long carId) {
        log.info("Adding photos to car ID: {}", carId);

        try {
            Car car = carRepository.findById(carId)
                    .orElseThrow(() -> {
                        log.error("Car not found with ID: {}", carId);
                        return new CarNotFoundException("Car with ID " + carId + " does not exist.");
                    });

            List<String> photoUrls = uploadPhotos(files, carId);
            car.getPhotos().addAll(photoUrls);
            carRepository.save(car);

            // Invalidate car cache
            invalidateCarCache(carId);

            log.info("Added {} photos to car ID: {}", photoUrls.size(), carId);

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new ApiResponseObject(
                            "Uploaded all photos",
                            true,
                            null
                    ));
        } catch (CarNotFoundException e) {
            throw e; // Re-throw specific exception
        } catch (Exception e) {
            log.error("Unexpected error adding photos to car ID: {}", carId, e);
            throw new DatabaseException("Error while adding photos to car. Please try again.");
        }
    }

    // Helper methods
    private List<String> uploadPhotos(List<MultipartFile> files, Long carId) {
        List<String> photoUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Map uploadedFile = cloudinaryService.upload(file);
                String photoUrl = (String) uploadedFile.get("url");

                if (photoUrl != null) {
                    photoUrls.add(photoUrl);
                } else {
                    log.warn("Failed to upload photo for car ID: {}", carId);
                }
            } catch (Exception e) {
                log.error("Error uploading photo for car ID: {}", carId, e);
                // Continue with other photos even if one fails
            }
        }

        return photoUrls;
    }

    private CarResponseDto buildCarResponseDto(Car car) {
        CarResponseDto responseDto = new CarResponseDto();
        responseDto.setCar(modelMapper.map(car, CarDto.class));
        if (car.getLocation() != null) {
            responseDto.setLocation(modelMapper.map(car.getLocation(), LocationDto.class));
        }
        return responseDto;
    }

    private void updateCarFields(Car car, CarDto carDto) {
        if (carDto.getMake() != null) car.setMake(carDto.getMake());
        if (carDto.getModel() != null) car.setModel(carDto.getModel());
        car.setYear(carDto.getYear());
        if (carDto.getColor() != null) car.setColor(carDto.getColor());
        if (carDto.getCarCategory() != null) car.setCarCategory(carDto.getCarCategory());
        if (carDto.getCarType() != null) car.setCarType(carDto.getCarType());
        car.setFuelType(carDto.getFuelType());
        car.setTransmissionMode(carDto.getTransmissionMode());
        car.setAvailabilityStatus(carDto.getAvailabilityStatus());
        car.setRentalPricePerDay(carDto.getRentalPricePerDay());
        car.setRentalPricePerWeek(carDto.getRentalPricePerWeek());
        car.setRentalPricePerMonth(carDto.getRentalPricePerMonth());
        if (carDto.getMaintenanceDueDate() != null) car.setMaintenanceDueDate(carDto.getMaintenanceDueDate());
        car.setSeatingCapacity(carDto.getSeatingCapacity());
        car.setLuggageCapacity(carDto.getLuggageCapacity());
    }

    private void updateCarLocation(Car car, LocationDto locationDto) {
        if (locationDto != null) {
            Location location = car.getLocation();

            if (location == null) {
                location = modelMapper.map(locationDto, Location.class);
                location.setCar(car);
                car.setLocation(location);
                locationRepository.save(location);
                log.debug("Created new location for car ID: {}", car.getCarId());
            } else {
                if (locationDto.getAddress() != null) location.setAddress(locationDto.getAddress());
                if (locationDto.getCity() != null) location.setCity(locationDto.getCity());
                location.setLatitude(locationDto.getLatitude());
                location.setLongitude(locationDto.getLongitude());
                log.debug("Updated existing location for car ID: {}", car.getCarId());
            }
        }
    }

    private void cacheCarData(String cacheKey, CarDto carDto) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.set(cacheKey, carDto, CACHE_TTL);
                    log.debug("Cached car data for key: {}", cacheKey);
                } catch (Exception e) {
                    log.error("Failed to cache car data for key: {}", cacheKey, e);
                }
            });
        }
    }

    private void cacheCarList(String cacheKey, List<?> carList) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.setList(cacheKey, carList, CACHE_TTL);
                    log.debug("Cached car list for key: {}", cacheKey);
                } catch (Exception e) {
                    log.error("Failed to cache car list for key: {}", cacheKey, e);
                }
            });
        }
    }

    private void invalidateCarCache(Long carId) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.delete(CACHE_KEY_PREFIX + carId);
                    log.debug("Invalidated cache for car ID: {}", carId);
                } catch (Exception e) {
                    log.error("Failed to invalidate cache for car ID: {}", carId, e);
                }
            });
        }
    }

    private void invalidateHostCarCache(String email) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.delete(CACHE_KEY_PREFIX + "host:" + email);
                    log.debug("Invalidated host cars cache for email: {}", email);
                } catch (Exception e) {
                    log.error("Failed to invalidate host cars cache for email: {}", email, e);
                }
            });
        }
    }

    private void invalidateCarCaches(Car car) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    // Invalidate individual car cache
                    redisService.delete(CACHE_KEY_PREFIX + car.getCarId());

                    // Invalidate host's cars cache
                    redisService.delete(CACHE_KEY_PREFIX + "host:" + car.getHost().getEmail());

                    log.debug("Invalidated caches for car ID: {}", car.getCarId());
                } catch (Exception e) {
                    log.error("Failed to invalidate caches for car ID: {}", car.getCarId(), e);
                }
            });
        }
    }
}