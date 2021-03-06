package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT a from Rating a WHERE a.userId = :userId")
    List<Rating> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Rating r WHERE r.userId = :userId AND r.eventId = :eventId")
    List<Rating> findByUserAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT r FROM Rating r WHERE r.userVoterId = :userVoterId AND r.eventId = :eventId")
    List<Rating> findByUserVoterAndEventId(@Param("userVoterId") Long userVoterId, @Param("eventId") Long eventId);
}
