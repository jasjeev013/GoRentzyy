package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import org.springframework.http.ResponseEntity;

public interface CarService {

    ResponseEntity<ApiResponseObject> addNewCar(CarDto carDto,String email);
    ResponseEntity<ApiResponseObject> updateCar(CarDto carDto, Long carId);
    ResponseEntity<ApiResponseObject> getCarById(Long carId);
    ResponseEntity<ApiResponseObject> removeCar(Long carId);
    ResponseEntity<ApiResponseData> getAllCarsForSpecificHost(String email);
}
