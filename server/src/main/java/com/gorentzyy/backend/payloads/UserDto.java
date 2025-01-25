package com.gorentzyy.backend.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gorentzyy.backend.config.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {


    private Long userId;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;


    private AppConstants.Role role;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String socialLoginId;


    private byte[] profilePicture;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

   /*
    private List<Car> cars;
    private List<Booking> bookings;
    private List<Review> reviews;
    private List<Notification> notifications;
    */


}
