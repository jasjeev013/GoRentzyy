package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String emailId);
    Optional<User> findByPasswordResetToken(String token);
    boolean existsByPhoneNumber(String phoneNumber);
    @Query("SELECT u.emailVerified FROM User u WHERE u.email = :email")
    Optional<Boolean> isEmailVerifiedByEmail(@Param("email") String email);
}
