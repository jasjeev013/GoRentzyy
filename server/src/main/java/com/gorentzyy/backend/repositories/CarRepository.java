package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Car> findCarsByHostEmail(String email);
}
