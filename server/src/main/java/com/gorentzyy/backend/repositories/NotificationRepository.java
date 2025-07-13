package com.gorentzyy.backend.repositories;

import com.gorentzyy.backend.models.Notification;
import com.gorentzyy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> getByUserEmail(String email);
    List<Notification> findByUserOrderBySentAtDesc(User user);
}
