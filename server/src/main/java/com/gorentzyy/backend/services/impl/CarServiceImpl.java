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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CarServiceImpl implements CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final LocationRepository locationRepository;

    private final RedisService redisService;


    @Autowired
    public CarServiceImpl(UserRepository userRepository, CarRepository carRepository, ModelMapper modelMapper, CloudinaryService cloudinaryService, LocationRepository locationRepository, RedisService redisService) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
        this.locationRepository = locationRepository;
        this.redisService = redisService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Retryable(
            value = {DatabaseException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public ResponseEntity<ApiResponseObject> addNewCar(
            CarDto carDto,
            String email,
            List<MultipartFile> files,
            LocationDto locationDto) {

        try {
            // Step 1: Check if car already exists
            if (carRepository.existsByRegistrationNumber(carDto.getRegistrationNumber())) {
                logger.warn("Car with registration number {} already exists.", carDto.getRegistrationNumber());
                throw new CarAlreadyExistsException("A car with this registration already exists.");
            }

            // Step 2: Validate host
            User host = userRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("User with ID {} not found.", email);
                return new UserNotFoundException("User with ID " + email + " does not exist.");
            });

            // Step 3: Ensure only hosts can add cars
            if (host.getRole() == AppConstants.Role.RENTER) {
                logger.error("User with ID {} is not authorized to add cars.", email);
                throw new RoleNotAuthorizedException("Role Not Authorized to add cars");
            }

            // Step 4: Map CarDto to Car entity
            Car newCar = modelMapper.map(carDto, Car.class);
            newCar.setHost(host);
            host.getCars().add(newCar);

            // Step 5: Handle location if provided
            if (locationDto != null) {
                Location location = modelMapper.map(locationDto, Location.class);
                location.setCar(newCar);
                newCar.setLocation(location);
                locationRepository.save(location);
            }

            // Step 6: Set timestamps
            LocalDateTime now = LocalDateTime.now();
            newCar.setCreatedAt(now);
            newCar.setUpdatedAt(now);

            // Step 7: Save the new car first to get the ID
            Car savedCar = carRepository.save(newCar);

            // Step 8: Handle file uploads if files are present
            if (files != null && !files.isEmpty()) {
                List<String> photoUrls = new ArrayList<>();

                for (MultipartFile file : files) {
                    try {
                        Map uploadedFile = cloudinaryService.upload(file);
                        String photoUrl = (String) uploadedFile.get("url");

                        if (photoUrl != null) {
                            photoUrls.add(photoUrl);
                        } else {
                            logger.warn("Failed to upload one of the photos for car {}", savedCar.getCarId());
                        }
                    } catch (Exception e) {
                        logger.error("Error uploading photo for car {}: {}", savedCar.getCarId(), e.getMessage());
                        // Continue with other photos even if one fails
                    }
                }

                // Update the car with photo URLs
                savedCar.setPhotos(photoUrls);
                carRepository.save(savedCar);
            }

            logger.info("Car with registration number {} added successfully with {} photos.",
                    carDto.getRegistrationNumber(),
                    (files != null) ? files.size() : 0);

            // Prepare response with both car and location data
            CarResponseDto responseDto = new CarResponseDto();
            responseDto.setCar(modelMapper.map(savedCar, CarDto.class));
            if (savedCar.getLocation() != null) {
                responseDto.setLocation(modelMapper.map(savedCar.getLocation(), LocationDto.class));
            }

            return new ResponseEntity<>(new ApiResponseObject(
                    "The car was added successfully", true, responseDto
            ), HttpStatus.CREATED);

        } catch (CarAlreadyExistsException | UserNotFoundException | RoleNotAuthorizedException | InvalidCarDataException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw ex;  // Handled by GlobalExceptionHandler
        } catch (Exception e) {
            logger.error("Unexpected error while adding a new car: {}", e.getMessage());
            throw new DatabaseException("Error while saving the car to the database.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updateCar(
            CarDto carDto,
            Long carId,
            List<MultipartFile> files,
            LocationDto locationDto,
            String email) {

        try {
            // Step 1: Check if the car exists
            Car existingCar = carRepository.findById(carId).orElseThrow(() ->
                    new CarNotFoundException("Car with ID " + carId + " does not exist.")
            );

            // Step 2: Verify ownership
            if (!existingCar.getHost().getEmail().equals(email)) {
                throw new RoleNotAuthorizedException("You are not authorized to update this car");
            }

            // Step 3: Update the car details
            LocalDateTime now = LocalDateTime.now();
            existingCar.setUpdatedAt(now);
            existingCar.setMake(carDto.getMake() != null ? carDto.getMake() : existingCar.getMake());
            existingCar.setModel(carDto.getModel() != null ? carDto.getModel() : existingCar.getModel());
            existingCar.setYear(carDto.getYear());
            existingCar.setColor(carDto.getColor() != null ? carDto.getColor() : existingCar.getColor());
            existingCar.setCarCategory(carDto.getCarCategory() != null ? carDto.getCarCategory() : existingCar.getCarCategory());
            existingCar.setCarType(carDto.getCarType() != null ? carDto.getCarType() : existingCar.getCarType());
            existingCar.setFuelType(carDto.getFuelType());
            existingCar.setTransmissionMode(carDto.getTransmissionMode());
            existingCar.setAvailabilityStatus(carDto.getAvailabilityStatus());
            existingCar.setRentalPricePerDay(carDto.getRentalPricePerDay());
            existingCar.setRentalPricePerWeek(carDto.getRentalPricePerWeek());
            existingCar.setRentalPricePerMonth(carDto.getRentalPricePerMonth());
            existingCar.setMaintenanceDueDate(carDto.getMaintenanceDueDate() != null ? carDto.getMaintenanceDueDate() : existingCar.getMaintenanceDueDate());
            existingCar.setSeatingCapacity(carDto.getSeatingCapacity());
            existingCar.setLuggageCapacity(carDto.getLuggageCapacity());

            // Step 4: Handle location update
            if (locationDto != null) {
                Location existingLocation = existingCar.getLocation();

                if (existingLocation == null) {
                    // Create new location if it doesn't exist
                    Location newLocation = modelMapper.map(locationDto, Location.class);
                    newLocation.setCar(existingCar);
                    existingCar.setLocation(newLocation);
                    locationRepository.save(newLocation);
                } else {
                    // Update existing location
                    existingLocation.setAddress(locationDto.getAddress() != null ? locationDto.getAddress() : existingLocation.getAddress());
                    existingLocation.setCity(locationDto.getCity() != null ? locationDto.getCity() : existingLocation.getCity());
                    existingLocation.setLatitude(locationDto.getLatitude());
                    existingLocation.setLongitude(locationDto.getLongitude());
                }
            }

            // Step 5: Handle file uploads if files are present
            if (files != null && !files.isEmpty()) {
                List<String> photoUrls = existingCar.getPhotos() != null ?
                        new ArrayList<>(existingCar.getPhotos()) : new ArrayList<>();

                for (MultipartFile file : files) {
                    try {
                        Map uploadedFile = cloudinaryService.upload(file);
                        String photoUrl = (String) uploadedFile.get("url");

                        if (photoUrl != null) {
                            photoUrls.add(photoUrl);
                        } else {
                            logger.warn("Failed to upload one of the photos for car {}", existingCar.getCarId());
                        }
                    } catch (Exception e) {
                        logger.error("Error uploading photo for car {}: {}", existingCar.getCarId(), e.getMessage());
                        // Continue with other photos even if one fails
                    }
                }

                existingCar.setPhotos(photoUrls);
            }

            // Step 6: Save the updated car
            Car updatedCar = carRepository.save(existingCar);

            // Prepare response with both car and location data
            CarResponseDto responseDto = new CarResponseDto();
            responseDto.setCar(modelMapper.map(updatedCar, CarDto.class));
            if (updatedCar.getLocation() != null) {
                responseDto.setLocation(modelMapper.map(updatedCar.getLocation(), LocationDto.class));
            }

            return new ResponseEntity<>(new ApiResponseObject(
                    "Car updated successfully", true, responseDto
            ), HttpStatus.OK);

        } catch (CarNotFoundException | RoleNotAuthorizedException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw ex;
        } catch (Exception e) {
            logger.error("Unexpected error while updating car: {}", e.getMessage());
            throw new DatabaseException("Error while updating the car in the database.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getCarById(Long carId) {

        try {

            if (redisService != null) {
                Optional<CarDto> cachedCar = redisService.get(String.valueOf(carId), CarDto.class);
                if (cachedCar.isPresent()) {
                    logger.debug("User found in cache for carId: {}", carId);
                    return ResponseEntity.ok(
                            new ApiResponseObject("User found in cache", true, cachedCar.get())
                    );
                }
            }
            // Log the attempt to fetch the car
            logger.info("Attempting to fetch car with ID: {}", carId);

            // Check if car exists by ID, throw exception if not found
            Car existingCar = carRepository.findById(carId).orElseThrow(() ->
                    new CarNotFoundException("Car with ID " + carId + " does not exist.")
            );

            // Log successful retrieval of the car
            logger.info("Car with ID {} found.", carId);

            CarDto existingCarDto = modelMapper.map(existingCar, CarDto.class);

            if (redisService != null) {
                try {
                    // Cache asynchronously to not block the response
                    CompletableFuture.runAsync(() -> {
                        redisService.set(String.valueOf(carId), existingCarDto, Duration.ofMinutes(10));
                        logger.debug("Cached user data for userId: {}", carId);
                    });
                } catch (Exception e) {
                    logger.error("Failed to cache user data for carId: {}", carId, e);
                    // Don't fail the request if caching fails
                }
            }
            // Return the response with the car data
            return new ResponseEntity<>(new ApiResponseObject(
                    "The car is found", true,existingCarDto
            ), HttpStatus.OK);

        } catch (CarNotFoundException ex) {
            // Log error when car not found
            logger.error("Car with ID {} not found.", carId);
            throw ex;  // Will be handled by the GlobalExceptionHandler

        } catch (Exception e) {
            // Log any unexpected errors
            logger.error("Unexpected error while fetching car with ID {}: {}", carId, e.getMessage());
            throw new DatabaseException("An error occurred while retrieving the car.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> removeCar(Long carId) {
        try {
            // Log the attempt to delete the car
            logger.info("Attempting to delete car with ID: {}", carId);

            // Check if car exists by carId, using a more appropriate exception
            Car existingCar = carRepository.findById(carId).orElseThrow(() ->
                    new CarNotFoundException("Car with ID " + carId + " does not exist.")
            );

            // Log the successful retrieval of the car
            logger.info("Car with ID {} found for deletion.", carId);

            // Delete the car
            carRepository.delete(existingCar);

            // Log the successful deletion of the car
            logger.info("Car with ID {} deleted successfully.", carId);

            // Return a response after successful deletion
            return new ResponseEntity<>(new ApiResponseObject(
                    "Deleted Successfully", true, null
            ), HttpStatus.OK);  // Changed to 204 No Content

        } catch (CarNotFoundException ex) {
            // Log the error when the car is not found
            logger.error("Car with ID {} not found for deletion.", carId);
            throw ex;  // Will be handled by your GlobalExceptionHandler

        } catch (Exception e) {
            // Log any unexpected errors
            logger.error("Unexpected error while deleting car with ID {}: {}", carId, e.getMessage());
            throw new DatabaseException("An error occurred while deleting the car.");
        }
    }
    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificHost(String email) {
        try {

            if (redisService != null) {
                Optional<List<CarDto>> cachedCars = redisService.getList(email +"SpecificHost", CarDto.class);
                if (cachedCars.isPresent()) {
                    logger.debug("Bookings found in cache for email Id: {}", email);
                    return ResponseEntity.ok(
                            new ApiResponseData("Bookings found in cache", true, Collections.singletonList(cachedCars))
                    );
                }
            }
            // Step 1: Fetch the user and validate existence
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + email + " does not exist."));

            // Step 2: Check if the user is authorized as a HOST
            if (Objects.equals(user.getRole().toString(), "RENTER")) {
                throw new RoleNotAuthorizedException("User with ID " + email + " is not authorized.");
            }

            // Step 3: Fetch cars for the specific host
            List<Car> cars = carRepository.findCarsByHostEmail(email);

            // If no cars are found, return a 204 No Content response
            if (cars.isEmpty()) {
                logger.info("No cars found for host with ID {}", email);
                return ResponseEntity.noContent().build();  // Return 204 No Content with NO BODY
            }

            // Step 4: Map the car entities to CarDto objects
            List<CarDto> carDtos = cars.stream()
                    .map(car -> modelMapper.map(car, CarDto.class))
                    .toList();

            if (null!= redisService) {
                try {
                    // Cache asynchronously to not block the response
                    CompletableFuture.runAsync(() -> {
                        redisService.setList(email + "SpecificHost", carDtos, Duration.ofMinutes(10));
                        logger.debug("Cached user data for email Id: {}", email);
                    });
                } catch (Exception e) {
                    logger.error("Failed to cache user data for email Id: {}", email, e);
                    // Don't fail the request if caching fails
                }
            }

            // Log successful fetching of cars
            logger.info("Fetched {} cars for host with ID {}", carDtos.size(), email);

            // Step 5: Return the response with the list of car DTOs
            return ResponseEntity.ok(new ApiResponseData(
                    "All cars for the specific host found", true, Collections.singletonList(carDtos)
            ));

        } catch (UserNotFoundException | RoleNotAuthorizedException ex) {
            // Log and rethrow the known exceptions
            logger.error("Error fetching cars: {}", ex.getMessage());
            throw ex;  // Will be handled by your GlobalExceptionHandler

        } catch (Exception ex) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching cars for host with ID {}: {}", email, ex.getMessage());
            throw new DatabaseException("Error while fetching cars for the host. Please try again.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCars() {
        List<Car> cars = carRepository.findAll();

        List<CarDto> carDtos = cars.stream()
                .map(car -> modelMapper.map(car, CarDto.class))
                .toList();
        return ResponseEntity.ok(new ApiResponseData(
                "All cars found", true, Collections.singletonList(carDtos)
        ));
    }
    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificCity(String city) {
        List<Car> cars = carRepository.findCarsByLocation_City(city);
        List<CarDto> carDtos = cars.stream()
                .map(car -> modelMapper.map(car, CarDto.class))
                .toList();
        return ResponseEntity.ok(new ApiResponseData(
                "All cars found", true, Collections.singletonList(carDtos)
        ));
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForMakeAndModel(String make,String model) {
        List<Car> cars = carRepository.findCarsByMakeAndModel(make,model);
        List<CarDto> carDtos = cars.stream()
                .map(car -> modelMapper.map(car, CarDto.class))
                .toList();
        return ResponseEntity.ok(new ApiResponseData(
                "All cars found", true, Collections.singletonList(carDtos)
        ));
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificCityWithNotHavingStartDateANdEndDate(String city, LocalDateTime startDate, LocalDateTime endDate) {
        List<Car> cars = carRepository.findAvailableCarsInCityBetweenDates(city,startDate,endDate);
        List<CarDto> carDtos = cars.stream()
                .map(car -> modelMapper.map(car, CarDto.class))
                .toList();
        return ResponseEntity.ok(new ApiResponseData(
                "All cars found", true, Collections.singletonList(carDtos)
        ));
    }

    @Override
    public ResponseEntity<ApiResponseObject> addCarPhotos(List<MultipartFile> files, Long carId) {
        Car existingCar = carRepository.findById(carId).orElseThrow(() ->
                new UserNotFoundException("User with Email ID " + carId + " does not exist.")
        );
        System.out.println(files);
        for (MultipartFile file:files){
            Map savedPhoto = cloudinaryService.upload(file);

            String photoUrl = (String) savedPhoto.get("url");
            if (photoUrl != null) {
                existingCar.getPhotos().add(photoUrl);
            } else {
                throw new CloudinaryUploadException("Failed to upload photo.");
            }
        }
        carRepository.save(existingCar);
        return new ResponseEntity<>(new ApiResponseObject("Uploaded all photos",true,null),HttpStatus.ACCEPTED);
    }

}
