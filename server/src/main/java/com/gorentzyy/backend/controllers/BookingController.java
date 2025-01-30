package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.BookingDto;
import com.gorentzyy.backend.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create/{carId}/{renterId}")
    public ResponseEntity<ApiResponseObject> addBooking(@Valid @RequestBody BookingDto bookingDto, @PathVariable Long carId, @PathVariable Long renterId){
        return bookingService.createBooking(bookingDto,renterId,carId);
    }
    
    @PutMapping("/update/{bookingId}")
    public ResponseEntity<ApiResponseObject> updateBooking(@Valid @RequestBody BookingDto bookingDto,@PathVariable Long bookingId){
        return bookingService.updateBooking(bookingDto,bookingId);
    }
    
    @GetMapping("/get/{bookingId}")
    public ResponseEntity<ApiResponseObject> getBooking(@PathVariable Long bookingId){
        return bookingService.getBookingById(bookingId);
    }
    
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<ApiResponseObject> removeBooking(@PathVariable Long bookingId){
        return bookingService.cancelBooking(bookingId);
    }

    @GetMapping("/getByCar/{carId}")
    public ResponseEntity<ApiResponseData> getAllBookingsByCar(@PathVariable Long carId){
        return bookingService.getBookingsByCar(carId);
    }

    @GetMapping("/getByHost/{hostId}")
    public ResponseEntity<ApiResponseData> getAllBookingsForHost(@PathVariable Long hostId){
        return bookingService.getBookingsByHost(hostId);
    }
    @GetMapping("/getByRenter/{renterId}")
    public ResponseEntity<ApiResponseData> getAllBookingsOfRenter(@PathVariable Long renterId){
        return bookingService.getBookingsByRenter(renterId);
    }


}
