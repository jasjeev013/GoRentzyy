package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByRenter(User renter);
    List<Booking> findByCar(Car car);
    List<Booking> findByCarHost(User host);

    @Query("SELECT b FROM Booking b WHERE b.car.carId = :carId " +
            "AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Booking> findByCarIdAndDateRange(@Param("carId") Long carId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

}
