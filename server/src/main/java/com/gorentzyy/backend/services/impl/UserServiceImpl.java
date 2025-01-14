package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.DatabaseException;
import com.gorentzyy.backend.exceptions.PasswordHashingException;
import com.gorentzyy.backend.exceptions.UserAlreadyExistsException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<ApiResponseObject> createNewUser(UserDto userDto) {
        // Validate user input (check if the user already exists by email)
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("A user with this email already exists.");
        }

        // Map DTO to Entity
        User newUser = modelMapper.map(userDto, User.class);

        // Set the createdAt and updatedAt fields
        LocalDateTime now = LocalDateTime.now();
        newUser.setCreatedAt(now);
        newUser.setUpdatedAt(now);

        try {
            // Hash the password before saving the user
            String hashedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(hashedPassword);
        } catch (Exception e) {
            throw new PasswordHashingException("Failed to hash password.");
        }

        try {
            // Save the user to the database
            User savedUser = userRepository.save(newUser);
            return new ResponseEntity<>(new ApiResponseObject(
                    "User Created Successfully", true,
                    modelMapper.map(savedUser, UserDto.class)
            ), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new DatabaseException("Error while saving the user to the database.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> updateUser(UserDto userDto, Long userId) {
        // Check if user exists by userId, using a more appropriate exception
        User existingUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User with ID " + userId + " does not exist.")
        );

        // Update the user details
        LocalDateTime now = LocalDateTime.now();
        existingUser.setUpdatedAt(now);
        existingUser.setFullName(userDto.getFullName());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setAddress(userDto.getAddress());

        // Save the updated user
        User updatedUser = userRepository.save(existingUser);

        // Return a response with updated user information
        return new ResponseEntity<>(new ApiResponseObject(
                "User updated successfully", true, modelMapper.map(updatedUser, UserDto.class)
        ), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<ApiResponseObject> getUserById(Long userId) {
        // Check if user exists by userId, using a more appropriate exception
        User existingUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User with ID " + userId + " does not exist.")
        );

        // Return a response with user information
        return new ResponseEntity<>(new ApiResponseObject(
                "The user is found", true, modelMapper.map(existingUser, UserDto.class)
        ), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseObject> deleteUser(Long userId) {
        // Check if user exists by userId, using a more appropriate exception
        User existingUser = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User with ID " + userId + " does not exist.")
        );

        // Delete the user
        userRepository.delete(existingUser);

        // Return a response after successful deletion
        return new ResponseEntity<>(new ApiResponseObject(
                "Deleted Successfully", true, null
        ), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponseObject> getUserByEmail(String email) {
        // Check if user exists by email, using a more appropriate exception
        User existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email: " + email + " does not exist.")
        );

        // Return a response with user information
        return new ResponseEntity<>(new ApiResponseObject(
                "The user is found", true, modelMapper.map(existingUser, UserDto.class)
        ), HttpStatus.OK);
    }
}
