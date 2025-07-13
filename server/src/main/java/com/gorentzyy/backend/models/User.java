package com.gorentzyy.backend.models;

import com.gorentzyy.backend.constants.AppConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rentzyy_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;
    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    private String address;

    private Boolean emailVerified;
    private Boolean phoneNumberVerified;

    @Enumerated(EnumType.STRING)

    private AppConstants.Role role;

    private String password;
    private String socialLoginId;

    private String profilePicture;

    @Column(name = "password_reset_token",length = 512)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry")
    private Date passwordResetTokenExpiry;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Car> cars = new ArrayList<>();

    @OneToMany(mappedBy = "renter", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

}
