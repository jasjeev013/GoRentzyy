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
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarService.class);
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;


    @Autowired
    public CarServiceImpl(UserRepository userRepository, CarRepository carRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> addNewCar(CarDto carDto, String email) {

        try {
            // Check if car already exists
            if (carRepository.existsByRegistrationNumber(carDto.getRegistrationNumber())) {
                logger.warn("Car with registration number {} already exists.", carDto.getRegistrationNumber());
                throw new CarAlreadyExistsException("A car with this registration already exists.");
            }

            // Check if the host exists
            User host = userRepository.findByEmail(email).orElseThrow(() -> {
                logger.error("User with ID {} not found.", email);
                return new UserNotFoundException("User with ID " + email + " does not exist.");
            });

            // Check if the host's role is authorized (e.g., should be "HOST")
            if (host.getRole() == AppConstants.Role.RENTER) {
                logger.error("User with ID {} is not authorized to add cars.", email);
                throw new RoleNotAuthorizedException("Role Not Authorized to add cars");
            }

            // Validate the carDto (for example, check required fields)
            if (carDto.getModel() == null || carDto.getModel().isEmpty()) {
                throw new InvalidCarDataException("Car model cannot be empty or null.");
            }
            if (carDto.getRegistrationNumber() == null || carDto.getRegistrationNumber().isEmpty()) {
                throw new InvalidCarDataException("Car registration number cannot be empty or null.");
            }

            // Map CarDto to Car entity
            Car newCar = modelMapper.map(carDto, Car.class);

            // Set the host as the owner of the car (if necessary)
            newCar.setHost(host);
            // Add the new car to the host's list of cars
            host.getCars().add(newCar);

            LocalDateTime now = LocalDateTime.now();
            newCar.setCreatedAt(now);
            newCar.setUpdatedAt(now);

            // Log the car creation process
            logger.info("Adding new car with registration number {}", carDto.getRegistrationNumber());

            // Save the new car (this also updates the host's cars list)
            Car savedCar = carRepository.save(newCar);

            // Log the successful addition of the car
            logger.info("Car with registration number {} added successfully.", carDto.getRegistrationNumber());

            return new ResponseEntity<>(new ApiResponseObject(
                    "The car was added successfully", true, modelMapper.map(savedCar, CarDto.class)
            ), HttpStatus.CREATED);

        } catch (CarAlreadyExistsException | UserNotFoundException | RoleNotAuthorizedException | InvalidCarDataException ex) {
            // Handle specific exceptions
            logger.error("Error: {}", ex.getMessage());
            throw ex;  // These will be handled by your GlobalExceptionHandler
        } catch (Exception e) {
            // Handle unexpected errors
            logger.error("Unexpected error while adding a new car: {}", e.getMessage());
            throw new DatabaseException("Error while saving the car to the database.");
        }
    }
// Just while updation if the values are removed if changes all to none
    @Override
    public ResponseEntity<ApiResponseObject> updateCar(CarDto carDto, Long carId) {
        // Step 1: Check if the car exists in the database, and if not, throw a CarNotFoundException
        Car existingCar = carRepository.findById(carId).orElseThrow(() ->
                new CarNotFoundException("Car with ID " + carId + " does not exist.")
        );

        // Step 2: Update the car details
        LocalDateTime now = LocalDateTime.now();
        existingCar.setUpdatedAt(now);
        existingCar.setMake(carDto.getMake());
        existingCar.setModel(carDto.getModel());
        existingCar.setYear(carDto.getYear());
        existingCar.setColor(carDto.getColor());
        existingCar.setCategory(carDto.getCategory());
        existingCar.setFuelType(carDto.getFuelType());
        existingCar.setAvailabilityStatus(carDto.getAvailabilityStatus());
        existingCar.setRentalPricePerDay(carDto.getRentalPricePerDay());
        existingCar.setRentalPricePerWeek(carDto.getRentalPricePerWeek());
        existingCar.setRentalPricePerMonth(carDto.getRentalPricePerMonth());
        existingCar.setMaintenanceDueDate(carDto.getMaintenanceDueDate());
        existingCar.setSeatingCapacity(carDto.getSeatingCapacity());

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
            // Log the attempt to fetch the car
            logger.info("Attempting to fetch car with ID: {}", carId);

            // Check if car exists by ID, throw exception if not found
            Car existingCar = carRepository.findById(carId).orElseThrow(() ->
                    new CarNotFoundException("Car with ID " + carId + " does not exist.")
            );

            // Log successful retrieval of the car
            logger.info("Car with ID {} found.", carId);

            // Return the response with the car data
            return new ResponseEntity<>(new ApiResponseObject(
                    "The car is found", true, modelMapper.map(existingCar, CarDto.class)
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
// Response Entity Returning No COntent
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
            ), HttpStatus.NO_CONTENT);  // Changed to 204 No Content

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
// For RENTERS show unauthorized
    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificHost(String email) {

        try {
            // Step 1: Validate that the host exists
            boolean hostExists = userRepository.existsByEmail(email);
            if (!hostExists) {
                logger.warn("User with ID {} does not exist.", email);
                throw new UserNotFoundException("User with ID " + email + " does not exist.");
            }

            // Step 2: Fetch cars for the specific host
            List<Car> cars = carRepository.findCarsByHostEmail(email);

            // If no cars are found, return a 204 No Content response
            if (cars.isEmpty()) {
                logger.info("No cars found for host with ID {}", email);
                return new ResponseEntity<>(new ApiResponseData(
                        "No cars found for this host", true, null
                ), HttpStatus.NO_CONTENT);  // Return 204 No Content
            }

            // Step 3: Map the car entities to CarDto objects
            List<CarDto> carDtos = cars.stream()
                    .map(car -> modelMapper.map(car, CarDto.class))
                    .toList();

            // Log successful fetching of cars
            logger.info("Fetched {} cars for host with ID {}", carDtos.size(), email);

            // Step 4: Return the response with the list of car DTOs
            return new ResponseEntity<>(new ApiResponseData(
                    "All cars for the specific host found", true, Collections.singletonList(carDtos)
            ), HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            // Log and rethrow the UserNotFoundException
            logger.error("Error fetching cars: {}", ex.getMessage());
            throw ex;  // Will be handled by your GlobalExceptionHandler

        } catch (Exception ex) {
            // Log any other unexpected errors
            logger.error("Unexpected error while fetching cars for host with ID {}: {}", email, ex.getMessage());
            throw new DatabaseException("Error while fetching cars for the host. Please try again.");
        }
    }
}
