package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import com.gorentzyy.backend.payloads.LocationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface CarService {

    ResponseEntity<ApiResponseObject> addNewCar(CarDto carDto, String email, List<MultipartFile> files, LocationDto locationDto);
    ResponseEntity<ApiResponseObject> updateCar(CarDto carDto, Long carId, List<MultipartFile> files, LocationDto locationDto, String email);
    ResponseEntity<ApiResponseObject> getCarById(Long carId);
    ResponseEntity<ApiResponseObject> removeCar(Long carId);
    ResponseEntity<ApiResponseData> getAllCarsForSpecificHost(String email);
    ResponseEntity<ApiResponseData> getAllCars();
    ResponseEntity<ApiResponseData> getAllCarsForSpecificCity(String city);
    ResponseEntity<ApiResponseData> getAllCarsForMakeAndModel(String make,String model);
    ResponseEntity<ApiResponseData> getAllCarsForSpecificCityWithNotHavingStartDateANdEndDate(String city, LocalDateTime startDate, LocalDateTime endDate);
    ResponseEntity<ApiResponseObject> addCarPhotos(List<MultipartFile> files, Long carId);
}
