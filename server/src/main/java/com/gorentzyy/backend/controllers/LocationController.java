package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.LocationDto;
import com.gorentzyy.backend.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/location")
@Validated
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/create/{carId}")
    public ResponseEntity<ApiResponseObject> addLocation(@Valid @RequestBody LocationDto locationDto, @PathVariable Long carId){
        return locationService.addLocation(locationDto,carId);
    }

    @PutMapping("/update/{locationId}")
    public ResponseEntity<ApiResponseObject> updateLocation(@Valid @RequestBody LocationDto locationDto,@PathVariable Long locationId){
        return locationService.updateLocation(locationDto,locationId);
    }

    @GetMapping("/get/{locationId}")
    public ResponseEntity<ApiResponseObject> getLocation(@PathVariable Long locationId){
        return locationService.getLocation(locationId);
    }

    @DeleteMapping("/delete/{locationId}")
    public ResponseEntity<ApiResponseObject> deleteLocation(@PathVariable Long locationId){
        return locationService.deleteLocation(locationId);
    }
}
