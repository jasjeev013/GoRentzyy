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
import com.gorentzyy.backend.services.LocationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LocationServiceImpl implements LocationService {

    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final LocationRepository locationRepository;


    @Autowired
    public LocationServiceImpl(CarRepository carRepository, ModelMapper modelMapper, LocationRepository locationRepository) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.locationRepository = locationRepository;
    }



    @Override
    public ResponseEntity<ApiResponseObject> addLocation(LocationDto locationDto, Long carId) {
        // Check if the car exists
        Car car = carRepository.findById(carId).orElseThrow(() ->
                new CarNotFoundException("Car with ID " + carId + " does not exist.")
        );

        // Map DTO to Entity
        Location newLocation = modelMapper.map(locationDto, Location.class);
        newLocation.getCars().add(car);
        car.getLocations().add(newLocation);  // Associating car with location

        try {
            // Save the location and rely on cascading to save the car if configured
            Location savedLocation = locationRepository.save(newLocation);

            return new ResponseEntity<>(new ApiResponseObject(
                    "The Location has been created", true, modelMapper.map(savedLocation, LocationDto.class)),
                    HttpStatus.CREATED); // Use CREATED (201) for new resources
        } catch (Exception e) {
            throw new DatabaseException("Error while saving the location.");
        }
    }

// Fields get empty
    @Override
    public ResponseEntity<ApiResponseObject> updateLocation(LocationDto locationDto, Long locationId) {
        // Find the existing location
        Location existingLocation = locationRepository.findById(locationId).orElseThrow(() ->
                new LocationNotFoundException("Location with ID " + locationId + " does not exist.")
        );

        // Update the existing location with new values
        existingLocation.setAddress(locationDto.getAddress());
        existingLocation.setName(locationDto.getName());
        existingLocation.setLatitude(locationDto.getLatitude());
        existingLocation.setLongitude(locationDto.getLongitude());

        try {
            // Save the updated location
            Location updatedLocation = locationRepository.save(existingLocation);

            return new ResponseEntity<>(new ApiResponseObject(
                    "The Location is updated", true, modelMapper.map(updatedLocation, LocationDto.class)),
                    HttpStatus.ACCEPTED); // Use ACCEPTED (202) for updates
        } catch (Exception e) {
            throw new DatabaseException("Error while updating the location.");
        }
    }


    @Override
    public ResponseEntity<ApiResponseObject> getLocation(Long locationId) {
        // Find the location by ID
        Location existingLocation = locationRepository.findById(locationId).orElseThrow(() ->
                new LocationNotFoundException("Location with ID " + locationId + " does not exist.")
        );

        return new ResponseEntity<>(new ApiResponseObject(
                "The Location is retrieved", true, modelMapper.map(existingLocation, LocationDto.class)),
                HttpStatus.OK);
    }

// Not able to delete the location
    @Override
    public ResponseEntity<ApiResponseObject> deleteLocation(Long locationId) {
        // Find the location to delete
        Location existingLocation = locationRepository.findById(locationId).orElseThrow(() ->
                new LocationNotFoundException("Location with ID " + locationId + " does not exist.")
        );

        try {
            // Delete the location
            locationRepository.delete(existingLocation);
            return new ResponseEntity<>(new ApiResponseObject(
                    "The location is deleted successfully", true, null), HttpStatus.OK);
        } catch (Exception e) {
            throw new DatabaseException("Error while deleting the location.");
        }
    }


}
