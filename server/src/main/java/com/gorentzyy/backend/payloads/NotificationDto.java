package com.gorentzyy.backend.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long notificationId;
    private String message;
    private Type type;
    private LocalDateTime sentAt;

    /*
    private User user;
     */

    public enum Type {
        BOOKING_CONFIRMATION, PAYMENT_ALERT, REMINDER, OTHER
    }
}
