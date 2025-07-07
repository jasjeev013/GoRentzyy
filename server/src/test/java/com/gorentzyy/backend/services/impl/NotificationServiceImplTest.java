package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.Notification;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseData;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private NotificationDto notificationDto;
    private Notification notification;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialize test data
        notificationDto = new NotificationDto(
                "Test Title",
                "Test Message",
                AppConstants.Type.REMINDER,
                LocalDateTime.now()
        );
        notificationDto.setNotificationId(1L);

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        notification = new Notification();
        notification.setNotificationId(1L);
        notification.setTitle("Test Title");
        notification.setMessage("Test Message");
        notification.setType(AppConstants.Type.REMINDER);
        notification.setSentAt(LocalDateTime.now());
        notification.setUser(user);
    }

    @Test
    void addServerSideNotification_WhenValidInput_ShouldCreateNotification() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(notificationDto, Notification.class)).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.addServerSideNotification(notificationDto, "test@example.com");

        // Assert
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void addServerSideNotification_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                notificationService.addServerSideNotification(notificationDto, "test@example.com"));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void addNotification_WhenValidInput_ShouldCreateNotification() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(notificationDto, Notification.class)).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(notificationDto);

        // Act
        ResponseEntity<ApiResponseObject> response = notificationService.addNotification(
                notificationDto, "test@example.com");

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Notification Created Successfully", response.getBody().getMessage());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void updateNotification_WhenValidInput_ShouldUpdateNotification() {
        // Arrange
        NotificationDto updateDto = new NotificationDto();
        updateDto.setMessage("Updated Message");

        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(updateDto);

        // Act
        ResponseEntity<ApiResponseObject> response = notificationService.updateNotification(
                updateDto, 1L);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Notification updated successfully", response.getBody().getMessage());
        assertEquals("Updated Message", ((NotificationDto) response.getBody().getObject()).getMessage());

        verify(notificationRepository, times(1)).findById(anyLong());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void getNotification_WhenNotificationExists_ShouldReturnNotification() {
        // Arrange
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(notificationDto);

        // Act
        ResponseEntity<ApiResponseObject> response = notificationService.getNotification(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Notification fetched Successfully", response.getBody().getMessage());
        assertEquals(notificationDto, response.getBody().getObject());

        verify(notificationRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteNotification_WhenNotificationExists_ShouldDeleteNotification() {
        // Arrange
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.of(notification));
        doNothing().when(notificationRepository).delete(any(Notification.class));

        // Act
        ResponseEntity<ApiResponseObject> response = notificationService.deleteNotification(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The Notification deleted Successfully", response.getBody().getMessage());

        verify(notificationRepository, times(1)).findById(anyLong());
        verify(notificationRepository, times(1)).delete(any(Notification.class));
    }

    @Test
    void getAllNotificationForAUser_WhenUserHasNotifications_ShouldReturnNotifications() {
        // Arrange
        when(notificationRepository.getByUserEmail(anyString())).thenReturn(List.of(notification));
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(notificationDto);

        // Act
        ResponseEntity<ApiResponseData> response = notificationService.getAllNotificationForAUser("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("All cars found", response.getBody().getMessage());

        verify(notificationRepository, times(1)).getByUserEmail(anyString());
    }

    @Test
    void getAllNotificationForAUser_WhenNoNotifications_ShouldReturnEmptyList() {
        // Arrange
        when(notificationRepository.getByUserEmail(anyString())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ApiResponseData> response = notificationService.getAllNotificationForAUser("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals(0, ((List<?>) response.getBody().getData().get(0)).size());

        verify(notificationRepository, times(1)).getByUserEmail(anyString());
    }
}