package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import com.gorentzyy.backend.repositories.CarRepository;
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

    private final RedisService redisService;


    @Autowired
    public CarServiceImpl(UserRepository userRepository, CarRepository carRepository, ModelMapper modelMapper, CloudinaryService cloudinaryService, RedisService redisService) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.cloudinaryService = cloudinaryService;
        this.redisService = redisService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)  // Ensures rollback on all exceptions
    @Retryable(
            value = {DatabaseException.class},  // Retry only for DatabaseException
            maxAttempts = 3,  // Retry 3 times before failing
            backoff = @Backoff(delay = 2000, multiplier = 2)  // 2 sec delay, increasing exponentially
    )
    public ResponseEntity<ApiResponseObject> addNewCar(CarDto carDto, String email) {
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

            // Step 5: Map CarDto to Car entity
            Car newCar = modelMapper.map(carDto, Car.class);
            newCar.setHost(host);
            host.getCars().add(newCar);

            // Step 6: Set timestamps
            LocalDateTime now = LocalDateTime.now();
            newCar.setCreatedAt(now);
            newCar.setUpdatedAt(now);

            // Step 7: Save the new car
            Car savedCar = carRepository.save(newCar);

            logger.info("Car with registration number {} added successfully.", carDto.getRegistrationNumber());

            return new ResponseEntity<>(new ApiResponseObject(
                    "The car was added successfully", true, modelMapper.map(savedCar, CarDto.class)
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
    public ResponseEntity<ApiResponseObject> updateCar( CarDto carDto, Long carId) {
        // Step 1: Check if the car exists in the database, and if not, throw a CarNotFoundException
        Car existingCar = carRepository.findById(carId).orElseThrow(() ->
                new CarNotFoundException("Car with ID " + carId + " does not exist.")
        );

        // Step 2: Update the car details
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
        try {
            // Step 3: Attempt to save the updated car details to the database
            Car updatedCar = carRepository.save(existingCar);

            // Step 4: Return the response with the updated car information
            return new ResponseEntity<>(new ApiResponseObject(
                    "Updated Car Details Successfully", true, modelMapper.map(updatedCar, CarDto.class)
            ), HttpStatus.OK);

        } catch (Exception e) {
            // Step 5: Handle any database-related issues (e.g., saving to DB)
            throw new DatabaseException("Error while updating car details. Please try again.");
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
            existingCar.getPhotos().add((String) savedPhoto.get("url"));
        }
        carRepository.save(existingCar);
        return new ResponseEntity<>(new ApiResponseObject("Uploaded all photos",true,null),HttpStatus.ACCEPTED);
    }

}
