package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.Car;
import com.gorentzyy.backend.models.Review;
import com.gorentzyy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    boolean existsByReviewerAndCar(User reviewer, Car car);
}
