package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.CarDto;
import com.gorentzyy.backend.services.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/car")
@Validated
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/create/{hostId}")
    public ResponseEntity<ApiResponseObject> addNewCar(@Valid @RequestBody CarDto carDto, @PathVariable Long hostId){
        return carService.addNewCar(carDto,hostId);
    }

    @PutMapping("/update/{carId}")
    public ResponseEntity<ApiResponseObject> updateNewCar(@Valid @RequestBody CarDto carDto,@PathVariable Long carId){
        return carService.updateCar(carDto,carId);
    }

    @GetMapping("/get/{carId}")
    public ResponseEntity<ApiResponseObject> getCar(@PathVariable Long carId){
        return carService.getCarById(carId);
    }

    @DeleteMapping("/delete/{carId}")
    public ResponseEntity<ApiResponseObject> removeCar(@PathVariable Long carId){
        return carService.removeCar(carId);
    }

    @GetMapping("/getAll/{hostId}")
    public ResponseEntity<ApiResponseData> getAllCarsOfSpecificHost(@PathVariable Long hostId){
        return carService.getAllCarsForSpecificHost(hostId);
    }
}
