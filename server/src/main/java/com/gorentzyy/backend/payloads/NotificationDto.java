package com.gorentzyy.backend.payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gorentzyy.backend.constants.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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

    @NotBlank(message = "Notification message cannot be empty")
    private String message;

    @NotNull(message = "Notification type is required")
    private AppConstants.Type type;

    private boolean isRead;

    @JsonIgnore
    @PastOrPresent(message = "Sent date must be in the past or present")
    private LocalDateTime sentAt;

    /*
    private User user;
     */
}
