package com.gorentzyy.backend.payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gorentzyy.backend.config.AppConstants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    @NotBlank(message = "Name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid and contain 10 to 15 digits")
    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;

    private String address;

    @NotNull(message = "Role is required")
    private AppConstants.Role role;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    private String socialLoginId;

    @JsonIgnore
    private byte[] profilePicture;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*
    private List<CarDto> cars;
    private List<BookingDto> bookings;
    private List<ReviewDto> reviews;
    private List<NotificationDto> notifications;
    */
}