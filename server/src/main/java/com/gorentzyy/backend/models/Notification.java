package com.gorentzyy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String message;

    @Enumerated(EnumType.STRING)
    private Type type;

    private boolean isRead;
    private LocalDateTime sentAt;

    public enum Type {
        BOOKING_CONFIRMATION, PAYMENT_ALERT, REMINDER, OTHER
    }
}

