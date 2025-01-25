package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.NotificationNotFoundException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.Notification;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.NotificationDto;
import com.gorentzyy.backend.repositories.NotificationRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(UserRepository userRepository, ModelMapper modelMapper, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public ResponseEntity<ApiResponseObject> addNotification(NotificationDto notificationDto, Long userId) {
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        Notification newNotification = modelMapper.map(notificationDto, Notification.class);
        newNotification.setUser(newUser);
        newUser.getNotifications().add(newNotification);

        userRepository.save(newUser);
        Notification savedNotification = notificationRepository.save(newNotification);

        return new ResponseEntity<>(new ApiResponseObject(
                "Notification Created Successfully", true, modelMapper.map(savedNotification, NotificationDto.class)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseObject> updateNotification(NotificationDto notificationDto, Long notificationId) {
        Notification existingNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + notificationId + " not found"));

         existingNotification.setRead(notificationDto.isRead()); // Assuming you want to update read status
        existingNotification.setMessage(notificationDto.getMessage());
        Notification savedNotification = notificationRepository.save(existingNotification);

        return new ResponseEntity<>(new ApiResponseObject(
                "The Notification updated successfully", true,
                modelMapper.map(savedNotification, NotificationDto.class)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseObject> getNotification(Long notificationId) {
        Notification existingNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + notificationId + " not found"));

        return new ResponseEntity<>(new ApiResponseObject(
                "The Notification fetched Successfully", true,
                modelMapper.map(existingNotification, NotificationDto.class)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseObject> deleteNotification(Long notificationId) {
        Notification existingNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + notificationId + " not found"));

        notificationRepository.delete(existingNotification);

        return new ResponseEntity<>(new ApiResponseObject("The Notification deleted Successfully", true, null),
                HttpStatus.OK);
    }

}
