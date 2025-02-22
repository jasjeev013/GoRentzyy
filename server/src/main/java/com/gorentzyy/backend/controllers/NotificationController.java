package com.gorentzyy.backend.controllers;

import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.NotificationDto;
import com.gorentzyy.backend.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseObject> addNotification(@Valid @RequestBody NotificationDto notificationDto, Authentication authentication){
        String email = authentication.getName();
        return notificationService.addNotification(notificationDto,email);
    }

    @PutMapping("/update/{notificationId}")
    public ResponseEntity<ApiResponseObject> updateNotification(@Valid @RequestBody NotificationDto notificationDto, @PathVariable Long notificationId){
        return notificationService.updateNotification(notificationDto,notificationId);
    }

    @GetMapping("/get/{notificationId}")
    public ResponseEntity<ApiResponseObject> getNotification(@PathVariable Long notificationId){
        return notificationService.getNotification(notificationId);
    }

    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<ApiResponseObject> deleteNotification(@PathVariable Long notificationId){
        return notificationService.deleteNotification(notificationId);
    }
}
