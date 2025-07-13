package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.DatabaseException;
import com.gorentzyy.backend.exceptions.InvalidNotificationDataException;
import com.gorentzyy.backend.exceptions.NotificationNotFoundException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.Notification;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseData;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.NotificationDto;
import com.gorentzyy.backend.repositories.NotificationRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

   

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImpl(UserRepository userRepository,
                                   ModelMapper modelMapper,
                                   NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void addServerSideNotification(NotificationDto notificationDto, String email) {
        log.info("Adding server-side notification for user: {}", email);

        try {
            validateNotificationDto(notificationDto);
            User user = getUserByEmail(email);

            Notification notification = createNotification(notificationDto, user);
            notificationRepository.save(notification);

            log.info("Successfully added server-side notification ID: {} for user: {}",
                    notification.getNotificationId(), email);

        } catch (UserNotFoundException | InvalidNotificationDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding server-side notification for user: {}", email, e);
            throw new DatabaseException("Error occurred while adding notification");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> addNotification(NotificationDto notificationDto, String email) {
        log.info("Adding notification for user: {}", email);

        try {
            validateNotificationDto(notificationDto);
            User user = getUserByEmail(email);

            Notification notification = createNotification(notificationDto, user);
            Notification savedNotification = notificationRepository.save(notification);

            log.info("Successfully added notification ID: {} for user: {}",
                    savedNotification.getNotificationId(), email);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseObject(
                            "Notification created successfully",
                            true,
                            modelMapper.map(savedNotification, NotificationDto.class))
                    );

        } catch (UserNotFoundException | InvalidNotificationDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding notification for user: {}", email, e);
            throw new DatabaseException("Error occurred while adding notification");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> updateNotification(NotificationDto notificationDto, Long notificationId) {
        log.info("Updating notification ID: {}", notificationId);

        try {
            validateNotificationDto(notificationDto);
            Notification notification = getNotificationById(notificationId);

            updateNotificationFields(notification, notificationDto);
            Notification updatedNotification = notificationRepository.save(notification);

            log.info("Successfully updated notification ID: {}", notificationId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Notification updated successfully",
                    true,
                    modelMapper.map(updatedNotification, NotificationDto.class))
            );

        } catch (NotificationNotFoundException | InvalidNotificationDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating notification ID: {}", notificationId, e);
            throw new DatabaseException("Error occurred while updating notification");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getNotification(Long notificationId) {
        log.info("Fetching notification ID: {}", notificationId);

        try {
            Notification notification = getNotificationById(notificationId);

            // Mark as read when fetched
            
        

            log.info("Successfully fetched notification ID: {}", notificationId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Notification retrieved successfully",
                    true,
                    modelMapper.map(notification, NotificationDto.class))
            );

        } catch (NotificationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching notification ID: {}", notificationId, e);
            throw new DatabaseException("Error occurred while retrieving notification");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseObject> deleteNotification(Long notificationId) {
        log.info("Deleting notification ID: {}", notificationId);

        try {
            Notification notification = getNotificationById(notificationId);
            notificationRepository.delete(notification);

            log.info("Successfully deleted notification ID: {}", notificationId);

            return ResponseEntity.ok(new ApiResponseObject(
                    "Notification deleted successfully",
                    true,
                    null)
            );

        } catch (NotificationNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting notification ID: {}", notificationId, e);
            throw new DatabaseException("Error occurred while deleting notification");
        }
    }

    @Override
    public ResponseEntity<ApiResponseData> getAllNotificationForAUser(String email) {
        log.info("Fetching all notifications for user: {}", email);

        try {
            User user = getUserByEmail(email);
            List<Notification> notifications = notificationRepository.findByUserOrderBySentAtDesc(user);

            List<NotificationDto> notificationDtos = notifications.stream()
                    .map(notification -> modelMapper.map(notification, NotificationDto.class))
                    .toList();

            log.info("Found {} notifications for user: {}", notificationDtos.size(), email);

            return ResponseEntity.ok(new ApiResponseData(
                    "Notifications retrieved successfully",
                    true,
                    Collections.singletonList(notificationDtos))
            );

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching notifications for user: {}", email, e);
            throw new DatabaseException("Error occurred while retrieving notifications");
        }
    }

    // Helper methods
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UserNotFoundException("User with email " + email + " not found");
                });
    }

    private Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.error("Notification not found with ID: {}", notificationId);
                    return new NotificationNotFoundException("Notification with ID " + notificationId + " not found");
                });
    }

    private void validateNotificationDto(NotificationDto notificationDto) {
        if (notificationDto == null) {
            throw new InvalidNotificationDataException("Notification data cannot be null");
        }
        if (!StringUtils.hasText(notificationDto.getMessage())) {
            throw new InvalidNotificationDataException("Notification message cannot be empty");
        }
        if (notificationDto.getType() == null) {
            throw new InvalidNotificationDataException("Notification type cannot be null");
        }
    }

    private Notification createNotification(NotificationDto notificationDto, User user) {
        Notification notification = modelMapper.map(notificationDto, Notification.class);
        notification.setUser(user);
        notification.setSentAt(LocalDateTime.now());
        return notification;
    }

    private void updateNotificationFields(Notification notification, NotificationDto notificationDto) {
        if (StringUtils.hasText(notificationDto.getMessage())) {
            notification.setMessage(notificationDto.getMessage());
        }
        if (notificationDto.getType() != null) {
            notification.setType(notificationDto.getType());
        }
    }
}