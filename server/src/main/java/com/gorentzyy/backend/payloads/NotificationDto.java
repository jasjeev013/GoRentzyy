package com.gorentzyy.backend.payloads;

import com.gorentzyy.backend.constants.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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
public class NotificationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long notificationId;

    @NotBlank(message = "Notification title cannot be empty")
    private String title;

    @NotBlank(message = "Notification message cannot be empty")
    private String message;

    @NotNull(message = "Notification type is required")
    private AppConstants.Type type;




    @PastOrPresent(message = "Sent date must be in the past or present")
    private LocalDateTime sentAt;

    /*
    private User user;
     */

    public NotificationDto(String title, String message, AppConstants.Type type, LocalDateTime sentAt) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.sentAt = sentAt;
    }
}
