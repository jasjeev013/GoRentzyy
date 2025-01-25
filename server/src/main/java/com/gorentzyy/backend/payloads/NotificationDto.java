package com.gorentzyy.backend.payloads;

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
public class NotificationDto {
    private Long notificationId;
    private String message;
    private AppConstants.Type type;
    private boolean isRead;
    private LocalDateTime sentAt;

    /*
    private User user;
     */


}
