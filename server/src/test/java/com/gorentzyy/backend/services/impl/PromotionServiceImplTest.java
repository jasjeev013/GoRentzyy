package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.BookingNotFoundException;
import com.gorentzyy.backend.exceptions.PromotionCodeAlreadyExistsException;
import com.gorentzyy.backend.exceptions.PromotionNotFoundException;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Promotion;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PromotionDto;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private PromotionDto promotionDto;
    private Promotion promotion;
    private Booking booking;

    @BeforeEach
    void setUp() {
        // Sample PromotionDto
        promotionDto = new PromotionDto();
        promotionDto.setCode("DISCOUNT50");
        promotionDto.setDiscountPercentage(50.0);
        promotionDto.setStartDate(LocalDateTime.now().plusDays(1));  // Future start date
        promotionDto.setEndDate(LocalDateTime.now().plusDays(10));   // Future end date
        promotionDto.setActive(true);
        promotionDto.setDescription("50% off on bookings");

        // Sample Promotion Entity
        promotion = new Promotion();
        promotion.setPromotionId(1L);
        promotion.setCode("DISCOUNT50");
        promotion.setDiscountPercentage(50.0);
        promotion.setStartDate(LocalDateTime.now().plusDays(1));
        promotion.setEndDate(LocalDateTime.now().plusDays(10));
        promotion.setActive(true);
        promotion.setDescription("50% off on bookings");

        // Sample Booking Entity
        booking = new Booking();
        booking.setBookingId(1L);
//        booking.setPromotions(new ArrayList<>());
    }

    // ✅ Test successful promotion addition
//    @Test
    void testAddPromotionCode_Success() {
        // Mock repository behavior
        when(promotionRepository.existsByCode(promotionDto.getCode())).thenReturn(false);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(modelMapper.map(promotionDto, Promotion.class)).thenReturn(promotion);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        ResponseEntity<ApiResponseObject> response = promotionService.addPromotionCode(promotionDto, 1L);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("The Promotion Code added successfully", response.getBody().getMessage());

        verify(bookingRepository, times(1)).findById(1L);
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    // ✅ Test adding duplicate promotion code
    @Test
    void testAddPromotionCode_DuplicateCode() {
        when(promotionRepository.existsByCode(promotionDto.getCode())).thenReturn(true);

        Exception exception = assertThrows(PromotionCodeAlreadyExistsException.class, () ->
                promotionService.addPromotionCode(promotionDto, 1L)
        );

        assertEquals("A promotion with this code already exists.", exception.getMessage());

        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    // ✅ Test adding promotion when booking does not exist
//    @Test
    void testAddPromotionCode_BookingNotFound() {
        when(promotionRepository.existsByCode(promotionDto.getCode())).thenReturn(false);
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(BookingNotFoundException.class, () ->
                promotionService.addPromotionCode(promotionDto, 1L)
        );

        assertEquals("Booking not found with ID 1", exception.getMessage());
    }

    // ✅ Test updating an existing promotion successfully
    @Test
    void testUpdatePromotionCode_Success() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        ResponseEntity<ApiResponseObject> response = promotionService.updatePromotionCode(promotionDto, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("The Promotion Code Updated successfully", response.getBody().getMessage());

        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    // ✅ Test updating a promotion that does not exist
    @Test
    void testUpdatePromotionCode_PromotionNotFound() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PromotionNotFoundException.class, () ->
                promotionService.updatePromotionCode(promotionDto, 1L)
        );

        assertEquals("Promotion not found with ID 1", exception.getMessage());
    }

    // ✅ Test fetching an existing promotion
    @Test
    void testGetPromotionCode_Success() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(modelMapper.map(promotion, PromotionDto.class)).thenReturn(promotionDto);

        ResponseEntity<ApiResponseObject> response = promotionService.getPromotionCode(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("The Promotion Data fetched Successfully", response.getBody().getMessage());

        verify(promotionRepository, times(1)).findById(1L);
    }

    // ✅ Test fetching a promotion that does not exist
    @Test
    void testGetPromotionCode_NotFound() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PromotionNotFoundException.class, () ->
                promotionService.getPromotionCode(1L)
        );

        assertEquals("Promotion not found with ID 1", exception.getMessage());
    }

    // ✅ Test deleting an existing promotion
    @Test
    void testDeletePromotionCode_Success() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        ResponseEntity<ApiResponseObject> response = promotionService.deletePromotionCode(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isResult());
        assertEquals("The promotion has been deleted successfully", response.getBody().getMessage());

        verify(promotionRepository, times(1)).delete(promotion);
    }

    // ✅ Test deleting a promotion that does not exist
    @Test
    void testDeletePromotionCode_NotFound() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PromotionNotFoundException.class, () ->
                promotionService.deletePromotionCode(1L)
        );

        assertEquals("Promotion not found with ID 1", exception.getMessage());
    }
}
