package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion,Long> {
    boolean existsByCode(String code);
}
