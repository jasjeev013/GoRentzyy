package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import com.gorentzyy.backend.payloads.LocationDto;
import com.gorentzyy.backend.services.CarService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/car")
@Validated
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

//    Working
@PreAuthorize("hasRole('HOST')")
@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ApiResponseObject> addNewCar(
        @RequestPart(value = "files" ,required = false) List<MultipartFile> files,
        @Valid @RequestPart("carDto") CarDto carDto,
        @RequestPart(value = "locationDto", required = false) LocationDto locationDto,
        Authentication authentication) {

    String email = authentication.getName();
    return carService.addNewCar(carDto, email, files, locationDto);
}

    @PreAuthorize("hasRole('HOST')")
    @PutMapping(value = "/update/{carId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseObject> updateCar(
            @PathVariable Long carId,
            @Valid @RequestPart("carDto") CarDto carDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "locationDto", required = false) LocationDto locationDto,
            Authentication authentication) {

        String email = authentication.getName();
        return carService.updateCar(carDto, carId, files, locationDto, email);
    }


    @GetMapping("/get/{carId}")
    public ResponseEntity<ApiResponseObject> getCar(@PathVariable Long carId){
        return carService.getCarById(carId);
    }

    @PreAuthorize("hasRole('HOST')")
    @DeleteMapping("/delete/{carId}")
    public ResponseEntity<ApiResponseObject> removeCar(@PathVariable Long carId){
        return carService.removeCar(carId);
    }

    @GetMapping("/getAllSpecific")
    public ResponseEntity<ApiResponseData> getAllCarsOfSpecificHost(Authentication authentication){
        String email = authentication.getName();
        return carService.getAllCarsForSpecificHost(email);
    }
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponseData> getAllCars(){
        return carService.getAllCars();
    }

    @GetMapping("/getByC")
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificCity(@RequestParam String city){
        System.out.println("getAllCarsForSpecificCity");
        return carService.getAllCarsForSpecificCity(city);
    }

    @GetMapping("/getByMM")
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificMakeAndModel(@RequestParam String make,@RequestParam String model){
        return carService.getAllCarsForMakeAndModel(make,model);
    }
    @GetMapping("/getByCT")
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificCityAndTimeline(@RequestParam String city, @RequestParam LocalDateTime startDate,LocalDateTime endDate){
        System.out.println(city + " " + startDate + " " + endDate);
        return carService.getAllCarsForSpecificCityWithNotHavingStartDateANdEndDate(city,startDate,endDate);
    }

    @PostMapping("/addPhotos/{carId}")
    public ResponseEntity<ApiResponseObject> addPhotos(@RequestParam("files") List<MultipartFile> files,@PathVariable Long carId) {
        System.out.println(files);
        return carService.addCarPhotos(files,carId);
    }
}
