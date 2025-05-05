package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.LocationDto;
import org.springframework.http.ResponseEntity;

public interface LocationService {

    ResponseEntity<ApiResponseObject> addLocation(LocationDto locationDto,Long carId,String email);
    ResponseEntity<ApiResponseObject> updateLocation(LocationDto locationDto,Long locationId);
    ResponseEntity<ApiResponseObject> getLocation(Long locationId);
    ResponseEntity<ApiResponseObject> deleteLocation(Long locationId);
}
