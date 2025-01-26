package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.BookingNotFoundException;
import com.gorentzyy.backend.exceptions.DatabaseException;
import com.gorentzyy.backend.exceptions.PromotionCodeAlreadyExistsException;
import com.gorentzyy.backend.exceptions.PromotionNotFoundException;
import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Promotion;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PromotionDto;
import com.gorentzyy.backend.repositories.BookingRepository;
import com.gorentzyy.backend.repositories.PromotionRepository;
import com.gorentzyy.backend.services.PromotionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class PromotionServiceImpl implements PromotionService {

    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private final PromotionRepository promotionRepository;

    @Autowired
    public PromotionServiceImpl(ModelMapper modelMapper, BookingRepository bookingRepository, PromotionRepository promotionRepository) {
        this.modelMapper = modelMapper;
        this.bookingRepository = bookingRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public ResponseEntity<ApiResponseObject> addPromotionCode(PromotionDto promotionDto, Long bookingId) {
        // Check if the promotion code already exists
        if (promotionRepository.existsByCode(promotionDto.getCode())) {
            throw new PromotionCodeAlreadyExistsException("A promotion with this code already exists.");
        }

        // Check if the booking exists
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID " + bookingId));

        // Map DTO to Entity
        Promotion newPromotion = modelMapper.map(promotionDto, Promotion.class);
        newPromotion.getBookings().add(existingBooking);
        existingBooking.getPromotions().add(newPromotion);

        try {
            // Save promotion to the database
            bookingRepository.save(existingBooking);
            Promotion savedPromotion = promotionRepository.save(newPromotion);

            return new ResponseEntity<>(new ApiResponseObject(
                    "The Promotion Code added successfully", true,
                    modelMapper.map(savedPromotion, PromotionDto.class)),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            throw new DatabaseException("Error occurred while adding promotion code.");
        }
    }



    @Override
    public ResponseEntity<ApiResponseObject> updatePromotionCode(PromotionDto promotionDto, Long promotionId) {
        // Find existing promotion by ID
        Promotion existingPromotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID " + promotionId));

        // Check if the code already exists (if it's being updated)
        if (!existingPromotion.getCode().equals(promotionDto.getCode()) &&
                promotionRepository.existsByCode(promotionDto.getCode())) {
            throw new PromotionCodeAlreadyExistsException("A promotion with this code already exists.");
        }

        // Update promotion details
        existingPromotion.setCode(promotionDto.getCode());
        existingPromotion.setDescription(promotionDto.getDescription());
        existingPromotion.setStartDate(promotionDto.getStartDate());
        existingPromotion.setEndDate(promotionDto.getEndDate());

        try {
            // Save updated promotion to the database
            Promotion savedPromotion = promotionRepository.save(existingPromotion);

            return new ResponseEntity<>(new ApiResponseObject(
                    "The Promotion Code Updated successfully", true,
                    modelMapper.map(savedPromotion, PromotionDto.class)),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new DatabaseException("Error occurred while updating promotion code.");
        }
    }




    @Override
    public ResponseEntity<ApiResponseObject> getPromotionCode(Long promotionId) {
        // Find existing promotion by ID
        Promotion existingPromotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID " + promotionId));

        return new ResponseEntity<>(new ApiResponseObject(
                "The Promotion Data fetched Successfully", true, modelMapper.map(existingPromotion, PromotionDto.class)),
                HttpStatus.OK);
    }


    @Override
    public ResponseEntity<ApiResponseObject> deletePromotionCode(Long promotionId) {
        // Find existing promotion by ID
        Promotion existingPromotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion not found with ID " + promotionId));

        try {
            // Delete the promotion
            promotionRepository.delete(existingPromotion);
            return new ResponseEntity<>(new ApiResponseObject("The promotion has been deleted successfully", true, null), HttpStatus.OK);
        } catch (Exception e) {
            throw new DatabaseException("Error occurred while deleting promotion code.");
        }
    }

}
