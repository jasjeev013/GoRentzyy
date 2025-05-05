package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import com.gorentzyy.backend.services.CarService;
import jakarta.validation.Valid;
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
    @PostMapping("/create")
    public ResponseEntity<ApiResponseObject> addNewCar(@Valid @RequestBody CarDto carDto, Authentication authentication){
        String email = authentication.getName();
        return carService.addNewCar(carDto,email);
    }

    @PreAuthorize("hasRole('HOST')")
    @PutMapping("/update/{carId}")
    public ResponseEntity<ApiResponseObject> updateNewCar(@Valid @RequestBody CarDto carDto,@PathVariable Long carId){
        return carService.updateCar(carDto,carId);
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
        return carService.getAllCarsForSpecificCity(city);
    }

    @GetMapping("/getByMM")
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificMakeAndModel(@RequestParam String make,@RequestParam String model){
        return carService.getAllCarsForMakeAndModel(make,model);
    }
    @GetMapping("/getByCT")
    public ResponseEntity<ApiResponseData> getAllCarsForSpecificCityAndTimeline(@RequestParam String city, @RequestParam LocalDateTime startDate,LocalDateTime endDate){
        return carService.getAllCarsForSpecificCityWithNotHavingStartDateANdEndDate(city,startDate,endDate);
    }

    @PostMapping("/addPhotos/{carId}")
    public ResponseEntity<ApiResponseObject> addPhotos(@RequestParam("files") List<MultipartFile> files,@PathVariable Long carId) {
        System.out.println(files);
        return carService.addCarPhotos(files,carId);
    }
}
