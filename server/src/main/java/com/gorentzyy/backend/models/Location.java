package com.gorentzyy.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    private String city;
    private String address;
    private double latitude;
    private double longitude;

    @OneToOne(mappedBy = "location")
    private Car car;
}

