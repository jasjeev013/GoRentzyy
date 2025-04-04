package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.exceptions.DatabaseException;
import com.gorentzyy.backend.exceptions.PasswordHashingException;
import com.gorentzyy.backend.exceptions.UserAlreadyExistsException;
import com.gorentzyy.backend.exceptions.UserNotFoundException;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.ApiResponseObject;
import com.gorentzyy.backend.payloads.UserDto;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.CloudinaryService;
import com.gorentzyy.backend.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private final CloudinaryService cloudinaryService;


//    private final Validator validator;


    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
//        this.validator = validator;
        this.cloudinaryService = cloudinaryService;
    }



    @Override
    public ResponseEntity<ApiResponseObject> createNewUser(UserDto userDto) {
        // Validate user input (check if the user already exists by email)
//        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto,UserDto.class);
//        if (!violations.isEmpty()) {
//            throw new ConstraintViolationException(violations);
//        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            logger.error("User already exists with email: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("A user with this email already exists.");
        }

        // Map DTO to Entity
        User newUser = modelMapper.map(userDto, User.class);
        System.out.println(newUser);
        // Set the createdAt and updatedAt fields
        LocalDateTime now = LocalDateTime.now();
        newUser.setCreatedAt(now);
        newUser.setUpdatedAt(now);

        try {
            // Hash the password before saving the user
            String hashedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(hashedPassword);
        } catch (BadCredentialsException e) {
            logger.error("Failed to hash password for user: {}", userDto.getEmail());
            throw new PasswordHashingException("Failed to hash password.");
        }

        try {
            // Save the user to the database
            User savedUser = userRepository.save(newUser);

            // Map the saved user back to DTO for response
            UserDto savedUserDto = modelMapper.map(savedUser, UserDto.class);

            // Return a successful response
            ApiResponseObject response = new ApiResponseObject(
                    "User Created Successfully", true, savedUserDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException | DatabaseException | ObjectOptimisticLockingFailureException e) {
            logger.error("Database integrity violation when saving user: {}", userDto.getEmail());
            throw new DatabaseException("Database integrity violation occurred while saving the user.");
        } catch (Exception e) {
            logger.error("Unexpected error during user creation for email: {}", userDto.getEmail(), e);
            throw new DatabaseException("Error while saving the user to the database.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> updateProfilePhoto(MultipartFile file, String emailId) {
        User existingUser = userRepository.findByEmail(emailId).orElseThrow(() ->
                new UserNotFoundException("User with Email ID " + emailId + " does not exist.")
        );

        Map savedPhoto = cloudinaryService.upload(file);

        existingUser.setProfilePicture((String) savedPhoto.get("url"));
        userRepository.save(existingUser);

        return new ResponseEntity<>(new ApiResponseObject(
                "Profile photo added successfully", true, null
        ), HttpStatus.ACCEPTED);
    }


    @Override
    public ResponseEntity<ApiResponseObject> updateUserByEmail(UserDto userDto, String emailId) {
        // Check if user exists by userId
        User existingUser = userRepository.findByEmail(emailId).orElseThrow(() ->
                new UserNotFoundException("User with Email ID " + emailId + " does not exist.")
        );

        // Update the user details
        LocalDateTime now = LocalDateTime.now();
        existingUser.setUpdatedAt(now);
        existingUser.setFullName(userDto.getFullName());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setAddress(userDto.getAddress());

        try {
            // Save the updated user
            User updatedUser = userRepository.save(existingUser);
            UserDto savedUserDto = modelMapper.map(updatedUser, UserDto.class);
            // Return a response with updated user information
            return new ResponseEntity<>(new ApiResponseObject(
                    "User updated successfully", true, modelMapper.map(updatedUser, UserDto.class)
            ), HttpStatus.ACCEPTED);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Error while updating user in the database.");
        } catch (Exception e) {
            throw new DatabaseException("An unexpected error occurred while updating the user.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getUserById(Long userId) {

        // Log the incoming request
        logger.info("Fetching user with ID: {}", userId);

        try {
            // Check if the user exists by userId
            User existingUser = userRepository.findById(userId).orElseThrow(() ->
                    new UserNotFoundException("User with ID " + userId + " does not exist.")
            );

            // Log the successful user retrieval
            logger.info("User with ID {} found.", userId);

            // Return a response with user information
            return new ResponseEntity<>(new ApiResponseObject(
                    "The user is found", true, modelMapper.map(existingUser, UserDto.class)
            ), HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            // Log the error for user not found
            logger.error("User with ID {} not found.", userId);
            throw ex;  // This will be handled by your GlobalExceptionHandler

        } catch (Exception e) {
            // Log unexpected errors
            logger.error("Unexpected error while fetching user with ID {}: {}", userId, e.getMessage());
            throw new DatabaseException("An error occurred while retrieving the user.");
        }
    }

    // Explanation: If return type: NO CONTENT then no response body should be returned
    @Override
    public ResponseEntity<ApiResponseObject> deleteUserByEmail(String email) {

        try {
            // Log the attempt to delete the user
            logger.info("Attempting to delete user with ID: {}", email);

            // Check if user exists by userId
            User existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("User with ID " + email + " does not exist.")
            );

            // Delete the user
            userRepository.delete(existingUser);

            // Log successful deletion
            logger.info("User with ID {} deleted successfully.", email);

            // Return a response after successful deletion
            return new ResponseEntity<>(new ApiResponseObject(
                    "Deleted Successfully", true, null
            ), HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            // Log the error for user not found
            logger.error("User with ID {} not found for deletion.", email);
            throw ex;  // Will be handled by the GlobalExceptionHandler

        } catch (DataIntegrityViolationException e) {
            // Log constraint violation errors (e.g., foreign key constraints)
            logger.error("Error deleting user with ID {}: Data integrity violation.", email);
            throw new DatabaseException("Cannot delete user due to database constraints.");

        } catch (Exception e) {
            // Log unexpected errors
            logger.error("Unexpected error while deleting user with ID {}: {}", email, e.getMessage());
            throw new DatabaseException("An error occurred while deleting the user.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getUserByEmail(String email) {


        try {
            // Log the incoming request to fetch the user by email
            logger.info("Attempting to retrieve user with email: {}", email);

            // Check if the user exists by email
            User existingUser = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("User with email: " + email + " does not exist.")
            );

            // Log the successful retrieval of the user
            logger.info("User with email {} found.", email);

            // Return a response with the user information
            return new ResponseEntity<>(new ApiResponseObject(
                    "The user is found", true, modelMapper.map(existingUser, UserDto.class)
            ), HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            // Log the error when user is not found
            logger.error("User with email {} not found.", email);
            throw ex;  // Will be handled by your GlobalExceptionHandler

        } catch (Exception e) {
            // Log any unexpected errors
            logger.error("Unexpected error while fetching user with email {}: {}", email, e.getMessage());
            throw new DatabaseException("An error occurred while retrieving the user.");
        }
    }
}
