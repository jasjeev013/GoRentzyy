package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.PromotionDto;
import org.springframework.http.ResponseEntity;

public interface PromotionService {

    ResponseEntity<ApiResponseObject> addPromotionCode(PromotionDto promotionDto,Long carId);
    ResponseEntity<ApiResponseObject> updatePromotionCode(PromotionDto promotionDto,Long promotionId);
    ResponseEntity<ApiResponseObject> getPromotionCode(Long promotionId);
    ResponseEntity<ApiResponseObject> deletePromotionCode(Long promotionId);

}
