package com.gorentzyy.backend.services;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.NotificationDto;
import org.springframework.http.ResponseEntity;

public interface NotificationService {

    ResponseEntity<ApiResponseObject> addNotification(NotificationDto notificationDto, String email);
    ResponseEntity<ApiResponseObject> updateNotification(NotificationDto notificationDto,Long notificationId);
    ResponseEntity<ApiResponseObject> getNotification(Long notificationDto);
    ResponseEntity<ApiResponseObject> deleteNotification(Long notificationId);

}
