package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import com.gorentzyy.backend.repositories.CarRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.CarService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

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
    public ResponseEntity<ApiResponseObject> addNewCar(CarDto carDto, Long hostId) {

        if (carRepository.existsByRegistrationNumber(carDto.getRegistrationNumber())) {
            throw new CarAlreadyExistsException("A car with this registration already exists.");
        }

        // Check if the host exists
        User host = userRepository.findById(hostId).orElseThrow(() ->
                new UserNotFoundException("User with ID " + hostId + " does not exist.")
        );

        // Check if the host's role is authorized (e.g., it should be "HOST")
        if (host.getRole().name().equals("RENTER")) {
            throw new RoleNotAuthorizedException("Role Not Authorized to add cars");
        }

        // Validate the carDto (for example, check required fields)
        if (carDto.getModel() == null || carDto.getModel().isEmpty()) {
            throw new InvalidCarDataException("Car model cannot be empty or null.");
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

        try {
            // Save the new car (this also updates the host's cars list)
            Car savedCar = carRepository.save(newCar);
            return new ResponseEntity<>(new ApiResponseObject(
                    "The Car added successfully", true, modelMapper.map(savedCar, CarDto.class)
            ), HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle potential database issues
            throw new DatabaseException("Error while saving the car to the database.");
        }
    }

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
        /* Uncomment and set other fields if needed
        existingCar.setCategory(carDto.getCategory());
        existingCar.setFuelType(carDto.getFuelType());
        existingCar.setAvailabilityStatus(carDto.getAvailabilityStatus());*/
        existingCar.setMaintenanceDueDate(carDto.getMaintenanceDueDate());
        existingCar.setSeatingCapacity(carDto.getSeatingCapacity());
        existingCar.setRentalPricePerDay(carDto.getRentalPricePerDay());

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
        Car exisitingCar = carRepository.findById(carId).orElseThrow(() ->
                new CarNotFoundException("Car with ID " + carId + " does not exist.")
        );
        return new ResponseEntity<>(new ApiResponseObject(
                "The car is found", true, modelMapper.map(exisitingCar, CarDto.class)
        ), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseObject> removeCar(Long carId) {
        // Check if car exists by carId, using a more appropriate exception
        Car exisitingCar = carRepository.findById(carId).orElseThrow(() ->
                new CarNotFoundException("Car with ID " + carId + " does not exist.")
        );

        // Delete the car
        carRepository.delete(exisitingCar);

        // Return a response after successful deletion
        return new ResponseEntity<>(new ApiResponseObject(
                "Deleted Successfully", true, null
        ), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificHost(Long hostId) {
        try {
            // Validate that the host exists
            // Assuming we have a user repository to check for the existence of the host
            boolean hostExists = userRepository.existsById(hostId);
            if (!hostExists) {
                throw new UserNotFoundException("User with ID " + hostId + " does not exist.");
            }

            // Step 2: Fetch cars for the specific host
            List<Car> cars = carRepository.findCarsByHostUserId(hostId);

            // Step 3: Map the car entities to CarDto objects
            List<CarDto> carDtos = cars.stream()
                    .map(car -> modelMapper.map(car, CarDto.class))
                    .toList();

            // Step 4: Return the response with the list of car DTOs
            return new ResponseEntity<>(new ApiResponseData(
                    "All Cars for specific host found", true, Collections.singletonList(carDtos)), HttpStatus.OK);

        }  catch (Exception ex) {
            // Handle any other exceptions (e.g., database issues, unexpected errors)
            throw new DatabaseException("Error while fetching cars for the host. Please try again.");
        }
    }
}
