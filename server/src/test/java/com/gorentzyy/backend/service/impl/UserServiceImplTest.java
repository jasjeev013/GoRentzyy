package com.gorentzyy.backend.service.impl;


import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.exceptions.UserAlreadyExistsException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest  {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFullName("Test User");
        userDto.setPhoneNumber("1234567890");
        userDto.setAddress("123 Street, City");
        userDto.setRole(AppConstants.Role.HOST);
        userDto.setPassword("password123");

        user = new User();
//        user.setId(1L);
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddress(userDto.getAddress());
        user.setPassword("hashedPassword");
        user.setRole(userDto.getRole());
    }

    /** Test case for creating a new user **/
    @Test
    void testCreateNewUser_Success() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        ResponseEntity<ApiResponseObject> response = userService.createNewUser(userDto);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User Created Successfully", response.getBody().getMessage());
    }

    /** Test case for creating a user that already exists **/
    @Test
    void testCreateNewUser_UserAlreadyExists() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createNewUser(userDto));
    }

    /** Test case for updating a user by email **/
    @Test
    void testUpdateUserByEmail_Success() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        ResponseEntity<ApiResponseObject> response = userService.updateUserByEmail(userDto, userDto.getEmail());

        assertNotNull(response);
        assertEquals(202, response.getStatusCodeValue());
        assertEquals("User updated successfully", response.getBody().getMessage());
    }

    /** Test case for updating a non-existing user **/
    @Test
    void testUpdateUserByEmail_UserNotFound() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserByEmail(userDto, userDto.getEmail()));
    }

    /** Test case for getting a user by ID **/
    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        ResponseEntity<ApiResponseObject> response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("The user is found", response.getBody().getMessage());
    }

    /** Test case for getting a non-existing user by ID **/
    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    /** Test case for deleting a user by email **/
    @Test
    void testDeleteUserByEmail_Success() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        ResponseEntity<ApiResponseObject> response = userService.deleteUserByEmail(userDto.getEmail());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Deleted Successfully", response.getBody().getMessage());
    }

    /** Test case for deleting a non-existing user **/
    @Test
    void testDeleteUserByEmail_UserNotFound() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserByEmail(userDto.getEmail()));
    }

    /** Test case for getting a user by email **/
    @Test
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        ResponseEntity<ApiResponseObject> response = userService.getUserByEmail(userDto.getEmail());

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("The user is found", response.getBody().getMessage());
    }

    /** Test case for getting a user by email when user is not found **/
    @Test
    void testGetUserByEmail_UserNotFound() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(userDto.getEmail()));
    }
}
