package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.BookingDto;
import com.gorentzyy.backend.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/booking")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('RENTER')")
    @PostMapping("/create/{carId}")
    public ResponseEntity<ApiResponseObject> addBooking(@Valid @RequestBody BookingDto bookingDto, @PathVariable Long carId, Authentication authentication){
        String renterEmail = authentication.getName();
        return bookingService.createBooking(bookingDto, renterEmail, carId);
    }

    @PreAuthorize("hasRole('RENTER')")
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

    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/getByHost")
    public ResponseEntity<ApiResponseData> getAllBookingsForHost(Authentication authentication){
        String hostEmail = authentication.getName();
        return bookingService.getBookingsByHost(hostEmail);
    }
    @PreAuthorize("hasRole('RENTER')")
    @GetMapping("/getByRenter")
    public ResponseEntity<ApiResponseData> getAllBookingsOfRenter(Authentication authentication){
        String renterEmail = authentication.getName();
        return bookingService.getBookingsByRenter(renterEmail);
    }


}
