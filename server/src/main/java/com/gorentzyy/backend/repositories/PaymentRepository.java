package com.gorentzyy.backend.repositories;


import com.gorentzyy.backend.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
