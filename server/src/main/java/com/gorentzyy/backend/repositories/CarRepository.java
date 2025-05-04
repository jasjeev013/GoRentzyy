package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
    List<Car> findCarsByHostEmail(String email);
    List<Car> findCarsByLocation_City(String city);
    List<Car> findCarsByMakeAndModel(String make,String model);

    @Query("SELECT c FROM Car c " +
            "WHERE c.location.city = :city " +
            "AND NOT EXISTS (" +
            "   SELECT b FROM Booking b " +
            "   WHERE b.car = c " +
            "   AND (" +
            "       b.startDate <= :endDate " +
            "       AND b.endDate >= :startDate" +
            "   )" +
            ")")
    List<Car> findAvailableCarsInCityBetweenDates(
            @Param("city") String city,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
