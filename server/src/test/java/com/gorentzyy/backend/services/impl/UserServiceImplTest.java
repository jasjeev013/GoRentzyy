package com.gorentzyy.backend.services.impl;


import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.*;
import com.gorentzyy.backend.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SMSService smsService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUserId(1L);
        userDto.setFullName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setPhoneNumber("+1234567890");
        userDto.setPassword("password");
        userDto.setRole(AppConstants.Role.RENTER);

        user = new User();
        user.setUserId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("+1234567890");
        user.setPassword("encodedPassword");
        user.setRole(AppConstants.Role.RENTER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createNewUser_WhenUserDoesNotExist_ShouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        // Act
        ResponseEntity<ApiResponseObject> response = userService.createNewUser(userDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("User Created Successfully", response.getBody().getMessage());
        assertEquals(userDto, response.getBody().getObject());

        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createNewUser_WhenUserAlreadyExists_ShouldThrowUserAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.createNewUser(userDto));
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createNewUser_WhenPasswordEncodingFails_ShouldThrowPasswordHashingException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Hashing failed"));

        // Act & Assert
        assertThrows(PasswordHashingException.class, () -> userService.createNewUser(userDto));
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void createNewUser_WhenDatabaseErrorOccurs_ShouldThrowDatabaseException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("DB error"));

        // Act & Assert
        assertThrows(DatabaseException.class, () -> userService.createNewUser(userDto));
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_WhenUserExistsInCache_ShouldReturnCachedUser() {
        // Arrange
        when(redisService.get(anyString(), eq(UserDto.class))).thenReturn(Optional.of(userDto));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("User found in cache", response.getBody().getMessage());
        assertEquals(userDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(UserDto.class));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserById_WhenUserNotInCacheButExistsInDB_ShouldReturnUserAndCacheIt() {
        // Arrange
        when(redisService.get(anyString(), eq(UserDto.class))).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        // Fix for CompletableFuture
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).set(anyString(), any(UserDto.class), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("The user is found", response.getBody().getMessage());
        assertEquals(userDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(UserDto.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(redisService, times(1)).set(anyString(), any(UserDto.class), any(Duration.class));
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(redisService.get(anyString(), eq(UserDto.class))).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        verify(redisService, times(1)).get(anyString(), eq(UserDto.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserByEmail_WhenUserExistsInCache_ShouldReturnCachedUser() {
        // Arrange
        when(redisService.get(anyString(), eq(UserDto.class))).thenReturn(Optional.of(userDto));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.getUserByEmail("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("User found in cache", response.getBody().getMessage());
        assertEquals(userDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(UserDto.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getUserByEmail_WhenUserNotInCacheButExistsInDB_ShouldReturnUserAndCacheIt() {
        // Arrange
        when(redisService.get(anyString(), eq(UserDto.class))).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        // Fix for CompletableFuture
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).set(anyString(), any(UserDto.class), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.getUserByEmail("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("User found successfully", response.getBody().getMessage());
        assertEquals(userDto, response.getBody().getObject());

        verify(redisService, times(1)).get(anyString(), eq(UserDto.class));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(redisService, times(1)).set(anyString(), any(UserDto.class), any(Duration.class));
    }


    @Test
    void getUserByEmail_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(redisService.get(anyString(), eq(UserDto.class))).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("test@example.com"));
        verify(redisService, times(1)).get(anyString(), eq(UserDto.class));
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void updateProfilePhoto_WhenUserExists_ShouldUpdatePhotoSuccessfully() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cloudinaryService.upload(any(MultipartFile.class))).thenReturn(java.util.Map.of("url", "http://newphoto.com"));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.updateProfilePhoto(file, "test@example.com");

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Profile photo added successfully", response.getBody().getMessage());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(cloudinaryService, times(1)).upload(any(MultipartFile.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateProfilePhoto_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateProfilePhoto(file, "test@example.com"));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(cloudinaryService, never()).upload(any(MultipartFile.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserByEmail_WhenUserExists_ShouldUpdateUserSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        // Act
        ResponseEntity<ApiResponseObject> response = userService.updateUserByEmail(userDto, "test@example.com", null);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("User updated successfully", response.getBody().getMessage());
        assertEquals(userDto, response.getBody().getObject());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserByEmail_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUserByEmail(userDto, "test@example.com", null));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserByEmail_WhenUserExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.deleteUserByEmail("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Deleted Successfully", response.getBody().getMessage());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void deleteUserByEmail_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserByEmail("test@example.com"));
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void sendOTPForEmailVerification_ShouldSendOTPSuccessfully() {
        // Arrange
        // Fix for CompletableFuture
        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(redisService).set(anyString(), anyString(), any(Duration.class));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.sendOTPForEmailVerification("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("OTP Sent successfully", response.getBody().getMessage());

        verify(redisService, times(1)).set(anyString(), anyString(), any(Duration.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void validateOTPForEmailVerification_WhenValidOTP_ShouldVerifyEmailSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisService.get(eq(email), eq(String.class))).thenReturn(Optional.of(otp));
        when(userRepository.findByEmail(eq(email))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<ApiResponseObject> response = userService.validateOTPForEmailVerification(email, otp);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isResult());
        assertEquals("Email Verified Successfully", response.getBody().getMessage());

        verify(redisService, times(1)).get(eq(email), eq(String.class));
        verify(userRepository, times(1)).findByEmail(eq(email));
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void validateOTPForEmailVerification_WhenInvalidOTP_ShouldReturnFailureResponse() {
        // Arrange
        String email = "test@example.com";
        String storedOTP = "123456";
        String providedOTP = "654321";
        when(redisService.get(eq(email), eq(String.class))).thenReturn(Optional.of(storedOTP));

        // Act
        ResponseEntity<ApiResponseObject> response = userService.validateOTPForEmailVerification(email, providedOTP);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isResult());
        assertEquals("OTP Expired", response.getBody().getMessage());

        verify(redisService, times(1)).get(eq(email), eq(String.class));
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}