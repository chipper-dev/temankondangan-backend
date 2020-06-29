package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Notification;
import com.mitrais.chipper.temankondangan.backendapps.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT a from Notification a WHERE a.userId = :userId")
    List<Notification> findByUserId(@Param("userId") Long userId);

    @Query("UPDATE Notification a set a.isRead = true WHERE a.userId = :userId")
    void changeAllNotificationToReadByUserId(@Param("userId") Long userId);
}
