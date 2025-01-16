package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.Booking;
import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByRenter(User renter);
    List<Booking> findByCar(Car car);
    List<Booking> findByCarHost(User host);
}
