package com.gorentzyy.backend.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDto {
    private CarDto car;
    private LocationDto location;
}
