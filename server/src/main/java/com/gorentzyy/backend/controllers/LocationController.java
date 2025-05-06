package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.LocationDto;
import com.gorentzyy.backend.services.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponseObject> addLocation(@Valid @RequestBody LocationDto locationDto, @PathVariable Long carId, Authentication authentication){
        String email = authentication.getName();
        return locationService.addLocation(locationDto,carId,email);
    }

    @PutMapping("/update/{locationId}")
    public ResponseEntity<ApiResponseObject> updateLocation(@Valid @RequestBody LocationDto locationDto,@PathVariable Long locationId,Authentication authentication){
        String email = authentication.getName();
        return locationService.updateLocation(locationDto,locationId,email);
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
