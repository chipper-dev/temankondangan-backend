package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT a from Notification a WHERE a.userId = :userId " +
            "AND (a.isRead = false " +
            "OR (a.isRead = true AND a.createdDate > :thirtyDays)) " +
            "ORDER BY a.createdDate DESC")
    List<Notification> findByUserId(@Param("userId") Long userId, @Param("thirtyDays") Date thirtyDays);

    @Query("SELECT count(a) from Notification a WHERE a.userId = :userId AND a.isRead = false")
    Integer countUnreadByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification a set a.isRead = true WHERE a.userId = :userId")
    void changeAllNotificationToReadByUserId(@Param("userId") Long userId);
}
