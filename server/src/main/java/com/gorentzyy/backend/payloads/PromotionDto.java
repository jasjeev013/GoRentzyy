package com.gorentzyy.backend.payloads;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long promotionId;

    @NotBlank(message = "Promotion code is required")
    @Size(min = 5, max = 20, message = "Code must be between 5 and 20 characters")
    private String code;

    @Min(value = 1, message = "Discount percentage must be at least 1%")
    @Max(value = 100, message = "Discount percentage cannot exceed 100%")
    private double discountPercentage;

    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    private boolean isActive;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
/*
    private List<Booking> bookings;
    */
}
