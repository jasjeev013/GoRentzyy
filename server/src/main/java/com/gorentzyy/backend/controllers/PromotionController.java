package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PromotionDto;
import com.gorentzyy.backend.services.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotion")
@Validated
public class PromotionController {

    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/create/{bookingId}")
    public ResponseEntity<ApiResponseObject> addPromotionCode(@Valid @RequestBody PromotionDto promotionDto, @PathVariable Long bookingId){
        return promotionService.addPromotionCode(promotionDto,bookingId);
    }

    @PutMapping("/update/{promotionId}")
    public ResponseEntity<ApiResponseObject> updatePromotion(@Valid @RequestBody PromotionDto promotionDto,@PathVariable Long promotionId){
        return promotionService.updatePromotionCode(promotionDto,promotionId);
    }

    @GetMapping("/get/{promotionId}")
    public ResponseEntity<ApiResponseObject> getPromotion(@PathVariable Long promotionId ){
        return promotionService.getPromotionCode(promotionId);
    }

    @DeleteMapping("/delete/{promotionId}")
    public ResponseEntity<ApiResponseObject> deletePromotion(@PathVariable Long promotionId){
        return promotionService.deletePromotionCode(promotionId);
    }
}
