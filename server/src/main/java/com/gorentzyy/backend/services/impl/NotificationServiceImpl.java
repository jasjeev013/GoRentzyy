package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.DatabaseException;
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
// Sent At is coming null
    @Override
    public ResponseEntity<ApiResponseObject> addNotification(NotificationDto notificationDto, Long userId) {
        // Validate the user
        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        // Map DTO to entity
        Notification newNotification = modelMapper.map(notificationDto, Notification.class);
        newNotification.setUser(newUser);
        newUser.getNotifications().add(newNotification); // Add notification to user's list

        try {
            // Save notification directly without saving user separately if no changes to the user
            Notification savedNotification = notificationRepository.save(newNotification);

            return new ResponseEntity<>(new ApiResponseObject(
                    "Notification Created Successfully", true, modelMapper.map(savedNotification, NotificationDto.class)),
                    HttpStatus.CREATED); // Use CREATED (201) instead of OK (200) for resource creation
        } catch (Exception e) {
            throw new DatabaseException("Error occurred while adding notification.");
        }
    }


    @Override
    public ResponseEntity<ApiResponseObject> updateNotification(NotificationDto notificationDto, Long notificationId) {
        Notification existingNotification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification with ID " + notificationId + " not found"));

        // Update notification fields
        existingNotification.setRead(notificationDto.isRead());
        existingNotification.setMessage(notificationDto.getMessage());

        try {
            Notification savedNotification = notificationRepository.save(existingNotification);

            return new ResponseEntity<>(new ApiResponseObject(
                    "The Notification updated successfully", true,
                    modelMapper.map(savedNotification, NotificationDto.class)),
                    HttpStatus.ACCEPTED); // Use ACCEPTED (202) for updates
        } catch (Exception e) {
            throw new DatabaseException("Error occurred while updating notification.");
        }
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

        try {
            notificationRepository.delete(existingNotification);
            return new ResponseEntity<>(new ApiResponseObject("The Notification deleted Successfully", true, null),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new DatabaseException("Error occurred while deleting notification.");
        }
    }


}
