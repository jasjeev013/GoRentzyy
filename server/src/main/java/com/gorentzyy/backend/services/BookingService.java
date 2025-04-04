package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.BookingDto;
import org.springframework.http.ResponseEntity;

public interface BookingService {

    ResponseEntity<ApiResponseObject> createBooking(BookingDto bookingDto,String emailId,Long carId);
    ResponseEntity<ApiResponseObject> updateBooking(BookingDto bookingDto,Long bookingId);
    ResponseEntity<ApiResponseObject> getBookingById(Long bookingId);
    ResponseEntity<ApiResponseObject> cancelBooking(Long bookingId);
    ResponseEntity<ApiResponseData> getBookingsByRenter(String emailId);
    ResponseEntity<ApiResponseData> getBookingsByCar(Long carId);
    ResponseEntity<ApiResponseData> getBookingsByHost(String emailId);


}
