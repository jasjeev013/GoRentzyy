package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.NotificationNotFoundException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.Notification;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.NotificationDto;
import com.gorentzyy.backend.repositories.NotificationRepository;
import com.gorentzyy.backend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setNotifications(new ArrayList<>());

        notification = new Notification();
        notification.setNotificationId(1L);
        notification.setMessage("Test Notification");
        notification.setUser(user);
        notification.setSentAt(LocalDateTime.now());
        notification.setRead(false);

        notificationDto = new NotificationDto();
        notificationDto.setMessage("Test Notification");
        notificationDto.setRead(false);
    }

    @Test
    void testAddNotification_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(modelMapper.map(notificationDto, Notification.class)).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        ResponseEntity<ApiResponseObject> response = notificationService.addNotification(notificationDto, user.getEmail());

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Notification Created Successfully", response.getBody().getMessage());
    }

    @Test
    void testAddNotification_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                notificationService.addNotification(notificationDto, user.getEmail())
        );
    }

    @Test
    void testUpdateNotification_Success() {
        when(notificationRepository.findById(notification.getNotificationId())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        ResponseEntity<ApiResponseObject> response = notificationService.updateNotification(notificationDto, notification.getNotificationId());

        assertNotNull(response);
        assertEquals(202, response.getStatusCodeValue());
        assertEquals("The Notification updated successfully", response.getBody().getMessage());
    }

    @Test
    void testUpdateNotification_NotFound() {
        when(notificationRepository.findById(notification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () ->
                notificationService.updateNotification(notificationDto, notification.getNotificationId())
        );
    }

    @Test
    void testGetNotification_Success() {
        when(notificationRepository.findById(notification.getNotificationId())).thenReturn(Optional.of(notification));
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(notificationDto);

        ResponseEntity<ApiResponseObject> response = notificationService.getNotification(notification.getNotificationId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("The Notification fetched Successfully", response.getBody().getMessage());
    }

    @Test
    void testGetNotification_NotFound() {
        when(notificationRepository.findById(notification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () ->
                notificationService.getNotification(notification.getNotificationId())
        );
    }

    @Test
    void testDeleteNotification_Success() {
        when(notificationRepository.findById(notification.getNotificationId())).thenReturn(Optional.of(notification));
        doNothing().when(notificationRepository).delete(notification);

        ResponseEntity<ApiResponseObject> response = notificationService.deleteNotification(notification.getNotificationId());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("The Notification deleted Successfully", response.getBody().getMessage());
    }

    @Test
    void testDeleteNotification_NotFound() {
        when(notificationRepository.findById(notification.getNotificationId())).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class, () ->
                notificationService.deleteNotification(notification.getNotificationId())
        );
    }
}

