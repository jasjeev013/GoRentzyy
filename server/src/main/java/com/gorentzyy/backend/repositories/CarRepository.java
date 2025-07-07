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
//    List<Car> findCarsByHostEmail(String email);
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

    @Query("""
    SELECT DISTINCT c FROM Car c
    LEFT JOIN FETCH c.location
    LEFT JOIN FETCH c.photos
    LEFT JOIN FETCH c.host h
    WHERE h.email = :email
""")
    List<Car> findCarsByHostEmailWithDetails(@Param("email") String email);

    @Query("select distinct c from Car c " +
            "join fetch c.host " +
            "left join fetch c.location " +
            "left join fetch c.bookings " +
            "left join fetch c.reviews " +
            "left join fetch c.promotions")
    List<Car> findAllWithRelations();

}
