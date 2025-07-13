package com.gorentzyy.backend.services.impl;

import com.gorentzyy.backend.constants.AppConstants;
import com.gorentzyy.backend.constants.EmailConstants;
import com.gorentzyy.backend.exceptions.*;
import com.gorentzyy.backend.models.LoginRequest;
import com.gorentzyy.backend.models.LoginResponse;
import com.gorentzyy.backend.models.User;
import com.gorentzyy.backend.payloads.*;
import com.gorentzyy.backend.repositories.UserRepository;
import com.gorentzyy.backend.services.*;
import com.gorentzyy.backend.utils.JwtUtils;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String CACHE_KEY_PREFIX = "user:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final JwtUtils jwtUtils;
    private final SMSService smsService;
    private final NotificationService notificationService;

    @Value("${app.reset-password.expiry-millis}")
    private long resetTokenExpiryMillis;

    @Value("${app.reset-password.deep-link-base}")
    private String deepLinkBase;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService,
                           EmailService emailService, AuthenticationManager authenticationManager,
                           RedisService redisService, JwtUtils jwtUtils, SMSService smsService,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.redisService = redisService;
        this.jwtUtils = jwtUtils;
        this.smsService = smsService;
        this.notificationService = notificationService;
    }

    @Override
    public ResponseEntity<ApiResponseObject> createNewUser(UserDto userDto) {
        logger.info("Attempting to create new user with email: {}", userDto.getEmail());

        try {
            // Validate email uniqueness
            if (userRepository.existsByEmail(userDto.getEmail())) {
                logger.warn("User creation failed - email already exists: {}", userDto.getEmail());
                throw new UserAlreadyExistsException("A user with this email already exists.");
            }

            // Map DTO to Entity
            User newUser = modelMapper.map(userDto, User.class);
            LocalDateTime now = LocalDateTime.now();

            // Set user properties
            newUser.setCreatedAt(now);
            newUser.setUpdatedAt(now);
            newUser.setEmailVerified(false);
            newUser.setPhoneNumberVerified(false);

            // Hash password
            try {
                String hashedPassword = passwordEncoder.encode(newUser.getPassword());
                newUser.setPassword(hashedPassword);
            } catch (Exception e) {
                logger.error("Password hashing failed for user: {}", userDto.getEmail(), e);
                throw new PasswordHashingException("Failed to hash password.");
            }

            // Save user
            User savedUser = userRepository.save(newUser);
            UserDto savedUserDto = modelMapper.map(savedUser, UserDto.class);

            // Send welcome email asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendEmail(savedUser.getEmail(),
                            EmailConstants.getNewUserCreatedSubject,
                            EmailConstants.getNewUserCreatedBody(savedUserDto.getFullName()));
                    logger.info("Welcome email sent to: {}", savedUser.getEmail());
                } catch (Exception e) {
                    logger.error("Failed to send welcome email to: {}", savedUser.getEmail(), e);
                }
            });

            logger.info("User created successfully with email: {}", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponseObject("User Created Successfully", true, savedUserDto));

        } catch (UserAlreadyExistsException | PasswordHashingException e) {
            throw e; // Re-throw specific exceptions
        } catch (DataIntegrityViolationException e) {
            logger.error("Database integrity violation when saving user: {}", userDto.getEmail(), e);
            throw new DatabaseException("Database integrity violation occurred while saving the user.");
        } catch (Exception e) {
            logger.error("Unexpected error during user creation for email: {}", userDto.getEmail(), e);
            throw new DatabaseException("Error while saving the user to the database.");
        }
    }

    @Override
    public ResponseEntity<LoginResponse> loginUser(LoginRequest loginRequest) {
        logger.info("Login attempt for username: {}", loginRequest.username());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            if (!authentication.isAuthenticated()) {
                logger.warn("Authentication failed for username: {}", loginRequest.username());
                throw new BadCredentialsException("Invalid username or password");
            }

            // Generate JWT token
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            String jwt = jwtUtils.createToken(authentication.getName(), role);

            // Send login notification asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendEmail(authentication.getName(),
                            EmailConstants.getUserLoginSubject,
                            EmailConstants.getUserLoginBody(authentication.getName()));

                    notificationService.addServerSideNotification(
                            new NotificationDto("Logged In Successfully",
                                    "A new login detected",
                                    AppConstants.Type.REMINDER,
                                    LocalDateTime.now()),
                            authentication.getName());

                    logger.info("Login notifications sent for user: {}", authentication.getName());
                } catch (Exception e) {
                    logger.error("Failed to send login notifications for user: {}", authentication.getName(), e);
                }
            });

            boolean emailVerified = userRepository.isEmailVerifiedByEmail(authentication.getName())
                    .orElse(false);

            logger.info("User {} logged in successfully", loginRequest.username());
            return ResponseEntity.ok(new LoginResponse(
                    HttpStatus.OK.getReasonPhrase(),
                    jwt,
                    role,
                    emailVerified));

        } catch (BadCredentialsException e) {
            logger.warn("Invalid login attempt for username: {}", loginRequest.username());
            throw new AuthenticationException("Invalid username or password");
        } catch (Exception e) {
            logger.error("Unexpected error during login for username: {}", loginRequest.username(), e);
            throw new AuthenticationException("Login failed due to technical error");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> updateProfilePhoto(MultipartFile file, String emailId) {
        logger.info("Updating profile photo for user: {}", emailId);

        try {
            User existingUser = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> new UserNotFoundException("User with Email ID " + emailId + " does not exist."));

            // Validate file
            if (file == null || file.isEmpty()) {
                logger.warn("Empty file provided for user: {}", emailId);
                throw new InvalidFileException("Profile photo file is required");
            }

            // Upload new photo
            Map uploadResult = cloudinaryService.upload(file);
            String photoUrl = (String) uploadResult.get("url");

            if (photoUrl == null) {
                logger.error("Cloudinary upload failed for user: {}", emailId);
                throw new CloudinaryUploadException("Failed to upload profile photo");
            }

            // Update user
            existingUser.setProfilePicture(photoUrl);
            userRepository.save(existingUser);

            // Invalidate cache
            invalidateUserCache(emailId);

            logger.info("Profile photo updated successfully for user: {}", emailId);
            return ResponseEntity.accepted()
                    .body(new ApiResponseObject("Profile photo added successfully", true, null));

        } catch (UserNotFoundException | InvalidFileException | CloudinaryUploadException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            logger.error("Error updating profile photo for user: {}", emailId, e);
            throw new DatabaseException("Error updating profile photo");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> updateUserByEmail(UserDto userDto, String emailId, MultipartFile multipartFile) {
        logger.info("Updating user profile for email: {}", emailId);

        try {
            User existingUser = userRepository.findByEmail(emailId)
                    .orElseThrow(() -> new UserNotFoundException("User with Email ID " + emailId + " does not exist."));

            // Update basic info
            existingUser.setUpdatedAt(LocalDateTime.now());
            existingUser.setFullName(userDto.getFullName());
            existingUser.setPhoneNumber(userDto.getPhoneNumber());
            existingUser.setAddress(userDto.getAddress());

            // Handle profile photo update if provided
            if (multipartFile != null && !multipartFile.isEmpty()) {
                validateProfilePhoto(multipartFile);

                Map uploadResult = cloudinaryService.upload(multipartFile);
                String photoUrl = (String) uploadResult.get("url");

                if (photoUrl == null) {
                    throw new CloudinaryUploadException("Failed to upload profile photo");
                }

                existingUser.setProfilePicture(photoUrl);
            }

            // Save updated user
            User updatedUser = userRepository.save(existingUser);
            UserDto savedUserDto = modelMapper.map(updatedUser, UserDto.class);

            // Invalidate cache
            invalidateUserCache(emailId);

            logger.info("User profile updated successfully for email: {}", emailId);
            return ResponseEntity.accepted()
                    .body(new ApiResponseObject("User updated successfully", true, savedUserDto));

        } catch (UserNotFoundException | InvalidFileTypeException |
                 FileSizeExceededException | CloudinaryUploadException e) {
            throw e; // Re-throw specific exceptions
        } catch (DataIntegrityViolationException e) {
            logger.error("Database error updating user: {}", emailId, e);
            throw new DatabaseException("Error while updating user in the database.");
        } catch (Exception e) {
            logger.error("Unexpected error updating user: {}", emailId, e);
            throw new DatabaseException("An unexpected error occurred while updating the user.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + userId;
            if (redisService != null) {
                Optional<UserDto> cachedUser = redisService.get(cacheKey, UserDto.class);
                if (cachedUser.isPresent()) {
                    logger.debug("Cache hit for user ID: {}", userId);
                    return ResponseEntity.ok(
                            new ApiResponseObject("User found in cache", true, cachedUser.get())
                    );
                }
            }

            // Cache miss - fetch from database
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " does not exist."));

            UserDto userDto = modelMapper.map(existingUser, UserDto.class);

            // Cache the result asynchronously
            if (redisService != null) {
                CompletableFuture.runAsync(() -> {
                    try {
                        redisService.set(cacheKey, userDto, CACHE_TTL);
                        logger.debug("Cached user data for ID: {}", userId);
                    } catch (Exception e) {
                        logger.error("Failed to cache user data for ID: {}", userId, e);
                    }
                });
            }

            logger.info("Successfully retrieved user with ID: {}", userId);
            return ResponseEntity.ok(
                    new ApiResponseObject("The user is found", true, userDto)
            );

        } catch (UserNotFoundException e) {
            logger.warn("User not found with ID: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching user with ID: {}", userId, e);
            throw new DatabaseException("An error occurred while retrieving the user.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> deleteUserByEmail(String email) {
        logger.info("Deleting user with email: {}", email);

        try {
            User existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with email " + email + " does not exist."));

            userRepository.delete(existingUser);

            // Invalidate cache
            invalidateUserCache(email);

            logger.info("Successfully deleted user with email: {}", email);
            return ResponseEntity.ok(
                    new ApiResponseObject("Deleted Successfully", true, null)
            );

        } catch (UserNotFoundException e) {
            logger.warn("User not found for deletion: {}", email);
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while deleting user: {}", email, e);
            throw new DatabaseException("Cannot delete user due to database constraints.");
        } catch (Exception e) {
            logger.error("Error deleting user: {}", email, e);
            throw new DatabaseException("An error occurred while deleting the user.");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> getUserByEmail(String email) {
        logger.info("Fetching user by email: {}", email);

        try {
            // Try cache first
            String cacheKey = CACHE_KEY_PREFIX + email;
            if (redisService != null) {
                Optional<UserDto> cachedUser = redisService.get(cacheKey, UserDto.class);
                if (cachedUser.isPresent()) {
                    logger.debug("Cache hit for user email: {}", email);
                    return ResponseEntity.ok(
                            new ApiResponseObject("User found in cache", true, cachedUser.get())
                    );
                }
            }

            // Cache miss - fetch from database
            User existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));

            UserDto userDto = modelMapper.map(existingUser, UserDto.class);

            // Cache the result asynchronously
            if (redisService != null) {
                CompletableFuture.runAsync(() -> {
                    try {
                        redisService.set(cacheKey, userDto, CACHE_TTL);
                        logger.debug("Cached user data for email: {}", email);
                    } catch (Exception e) {
                        logger.error("Failed to cache user data for email: {}", email, e);
                    }
                });
            }

            logger.info("Successfully retrieved user with email: {}", email);
            return ResponseEntity.ok(
                    new ApiResponseObject("User found successfully", true, userDto)
            );

        } catch (UserNotFoundException e) {
            logger.warn("User not found with email: {}", email);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching user with email: {}", email, e);
            throw new DatabaseException("Failed to retrieve user data");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> sendOTPForEmailVerification(String email) {
        logger.info("Sending OTP for email verification to: {}", email);

        try {
            // Check if user exists
            if (!userRepository.existsByEmail(email)) {
                logger.warn("Attempt to send OTP to non-existent email: {}", email);
                throw new UserNotFoundException("User with email " + email + " not found");
            }

            String token = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
            redisService.set(email, token, Duration.ofMinutes(2));

            // Send email asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendEmail(email, "Here's your OTP for GORentzyy", "OTP: " + token);
                    logger.info("OTP email sent to: {}", email);
                } catch (Exception e) {
                    logger.error("Failed to send OTP email to: {}", email, e);
                }
            });

            return ResponseEntity.ok(
                    new ApiResponseObject("OTP Sent successfully", true, null)
            );

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error sending OTP to: {}", email, e);
            throw new ServiceException("Failed to send OTP");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> validateOTPForEmailVerification(String email, String token) {
        logger.info("Validating OTP for email: {}", email);

        try {
            if (redisService == null) {
                logger.error("Redis service not available for OTP validation");
                throw new ServiceException("OTP validation service unavailable");
            }

            Optional<String> cachedTokenOpt = redisService.get(email, String.class);
            if (cachedTokenOpt.isEmpty() || !cachedTokenOpt.get().equals(token)) {
                logger.warn("Invalid or expired OTP for email: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponseObject("Invalid or expired OTP", false, null));
            }

            User existingUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));

            existingUser.setEmailVerified(true);
            userRepository.save(existingUser);

            // Remove used OTP from cache
            redisService.delete(email);

            // Send confirmation email asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendEmail(email, "Verified Email Successfully",
                            "Email is validated Successfully");
                    logger.info("Email verification confirmation sent to: {}", email);
                } catch (Exception e) {
                    logger.error("Failed to send verification confirmation to: {}", email, e);
                }
            });

            return ResponseEntity.ok(
                    new ApiResponseObject("Email Verified Successfully", true, null)
            );

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error validating OTP for email: {}", email, e);
            throw new ServiceException("Failed to validate OTP");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> sendOTpForPhoneNumberVerification(String phoneNumber) {
        logger.info("Sending OTP for phone number verification: {}", phoneNumber);

        try {
            // Check if phone number exists
            if (!userRepository.existsByPhoneNumber(phoneNumber)) {
                logger.warn("Attempt to send OTP to non-existent phone number: {}", phoneNumber);
                throw new UserNotFoundException("User with phone number " + phoneNumber + " not found");
            }

            // Send SMS asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    smsService.sendSMS(phoneNumber, "Hello from GoRentzyy");
                    logger.info("OTP SMS sent to: {}", phoneNumber);
                } catch (Exception e) {
                    logger.error("Failed to send OTP SMS to: {}", phoneNumber, e);
                }
            });

            return ResponseEntity.ok(
                    new ApiResponseObject("Message Sent", true, null)
            );

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error sending OTP to phone: {}", phoneNumber, e);
            throw new ServiceException("Failed to send OTP");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> validateOTPForPhoneNumberVerification(String phoneNumber, String token) {
        // TODO: Implement phone OTP validation similar to email OTP validation
        throw new UnsupportedOperationException("Phone number OTP validation not yet implemented");
    }

    @Override
    public ResponseEntity<ApiResponseObject> forgotPassword(ForgotPasswordRequest request) {
        logger.info("Processing forgot password request for email: {}", request.getEmail());

        try {
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

            // Always return OK to prevent email enumeration
            if (userOptional.isEmpty()) {
                logger.debug("Forgot password request for non-existent email: {}", request.getEmail());
                return ResponseEntity.ok().build();
            }

            User user = userOptional.get();
            String resetToken = jwtUtils.createToken(user.getEmail(), "PASSWORD_RESET");

            // Set token and expiry
            user.setPasswordResetToken(resetToken);
            user.setPasswordResetTokenExpiry(new Date(System.currentTimeMillis() + resetTokenExpiryMillis));
            userRepository.save(user);

            // Create deep link
            String deepLink = deepLinkBase + "?token=" + resetToken;
            String emailBody = "Click the following link to reset your password: " + deepLink +
                    "\n\nThis link will expire in 1 hour.";

            // Send email asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendEmail(user.getEmail(), "Password Reset Request", emailBody);
                    logger.info("Password reset email sent to: {}", user.getEmail());
                } catch (Exception e) {
                    logger.error("Failed to send password reset email to: {}", user.getEmail(), e);
                }
            });

            return ResponseEntity.ok(
                    new ApiResponseObject("Email sent for changing password", true, null)
            );

        } catch (Exception e) {
            logger.error("Error processing forgot password for email: {}", request.getEmail(), e);
            throw new ServiceException("Failed to process password reset request");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> validateResetToken(String token) {
        logger.info("Validating password reset token");

        try {
            User user = validatePasswordResetToken(token);
            if (user == null) {
                logger.warn("Invalid password reset token provided");
                return ResponseEntity.badRequest()
                        .body(new ApiResponseObject("Invalid or expired token", false, null));
            }

            return ResponseEntity.ok(
                    new ApiResponseObject("Validated Token", true, null)
            );

        } catch (Exception e) {
            logger.error("Error validating reset token", e);
            throw new ServiceException("Failed to validate reset token");
        }
    }

    @Override
    public ResponseEntity<ApiResponseObject> resetPassword(ResetPasswordRequest request) {
        logger.info("Processing password reset request");

        try {
            User user = validatePasswordResetToken(request.getToken());
            if (user == null) {
                logger.warn("Invalid password reset token provided");
                return ResponseEntity.badRequest()
                        .body(new ApiResponseObject("Invalid or expired token", false, null));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            userRepository.save(user);

            // Invalidate cache
            invalidateUserCache(user.getEmail());

            // Send confirmation email asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendEmail(user.getEmail(),
                            "Password Changed Successfully",
                            "Your password has been changed successfully.");
                    logger.info("Password change confirmation sent to: {}", user.getEmail());
                } catch (Exception e) {
                    logger.error("Failed to send password change confirmation to: {}", user.getEmail(), e);
                }
            });

            logger.info("Password reset successfully for user: {}", user.getEmail());
            return ResponseEntity.ok(
                    new ApiResponseObject("Changed Password Successfully", true, null)
            );

        } catch (Exception e) {
            logger.error("Error resetting password", e);
            throw new ServiceException("Failed to reset password");
        }
    }

    private User validatePasswordResetToken(String token) {
        try {
            if (!jwtUtils.validateToken(token)) {
                return null;
            }

            String email = jwtUtils.extractUsername(token);
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                return null;
            }

            User user = userOptional.get();
            if (!token.equals(user.getPasswordResetToken())) {
                return null;
            }

            if (user.getPasswordResetTokenExpiry().before(new Date())) {
                return null;
            }

            return user;
        } catch (Exception e) {
            logger.error("Error validating password reset token", e);
            return null;
        }
    }

    private void validateProfilePhoto(MultipartFile file) {
        if (!file.getContentType().startsWith("image/")) {
            throw new InvalidFileTypeException("Only image files are allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new FileSizeExceededException("File size exceeds maximum limit of 5MB");
        }
    }

    private void invalidateUserCache(String email) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.delete(CACHE_KEY_PREFIX + email);
                    logger.debug("Invalidated cache for user: {}", email);
                } catch (Exception e) {
                    logger.error("Failed to invalidate cache for user: {}", email, e);
                }
            });
        }
    }

    private void invalidateUserCache(Long userId) {
        if (redisService != null) {
            CompletableFuture.runAsync(() -> {
                try {
                    redisService.delete(CACHE_KEY_PREFIX + userId);
                    logger.debug("Invalidated cache for user ID: {}", userId);
                } catch (Exception e) {
                    logger.error("Failed to invalidate cache for user ID: {}", userId, e);
                }
            });
        }
    }
}