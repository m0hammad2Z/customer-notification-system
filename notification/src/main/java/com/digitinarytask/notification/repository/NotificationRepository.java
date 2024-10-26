package com.digitinarytask.notification.repository;

import com.digitinarytask.notification.domain.Notification;
import com.digitinarytask.shared.enumeration.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);
}
