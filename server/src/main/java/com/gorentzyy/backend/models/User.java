package com.gorentzyy.backend.models;

import com.gorentzyy.backend.constants.AppConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rentzyy_user")
public class User {
    public User(Long userId, String fullName, String email, String phoneNumber, String address, AppConstants.Role role, String password, String socialLoginId, byte[] profilePicture, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.password = password;
        this.socialLoginId = socialLoginId;
        this.profilePicture = profilePicture;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;
    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)

    private AppConstants.Role role;

    private String password;
    private String socialLoginId;

    @Lob
    private byte[] profilePicture;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "host", cascade = CascadeType.ALL)
    private List<Car> cars;

    @OneToMany(mappedBy = "renter", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;

}
