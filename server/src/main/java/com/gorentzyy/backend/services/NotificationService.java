package com.gorentzyy.backend.services;

import com.gorentzyy.backend.models.Notification;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.NotificationDto;
import com.gorentzyy.backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import java.util.List;

public interface NotificationService {

    ResponseEntity<ApiResponseObject> addNotification(NotificationDto notificationDto, Long userId);
    ResponseEntity<ApiResponseObject> updateNotification(NotificationDto notificationDto,Long notificationId);
    ResponseEntity<ApiResponseObject> getNotification(Long notificationDto);
    ResponseEntity<ApiResponseObject> deleteNotification(Long notificationId);

}
