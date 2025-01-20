package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.LocationDto;
import com.gorentzyy.backend.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/api/{carId}")
    public ResponseEntity<ApiResponseObject> addLocation(@RequestBody LocationDto locationDto, @PathVariable Long carId){
        return locationService.addLocation(locationDto,carId);
    }

    @PutMapping("/api/{locationId}")
    public ResponseEntity<ApiResponseObject> updateLocation(@RequestBody LocationDto locationDto,@PathVariable Long locationId){
        return locationService.updateLocation(locationDto,locationId);
    }

    @GetMapping("/api/get/{locationId}")
    public ResponseEntity<ApiResponseObject> getLocation(@PathVariable Long locationId){
        return locationService.getLocation(locationId);
    }

    @DeleteMapping("/api/delete/{locationId}")
    public ResponseEntity<ApiResponseObject> deleteLocation(@PathVariable Long locationId){
        return locationService.deleteLocation(locationId);
    }
}
