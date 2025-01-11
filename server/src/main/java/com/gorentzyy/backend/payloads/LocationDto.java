package com.gorentzyy.backend.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Long locationId;

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    /*
    private List<Car> cars;
     */
}
