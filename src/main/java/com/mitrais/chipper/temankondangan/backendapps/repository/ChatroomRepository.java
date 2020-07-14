package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("SELECT a from chatroom a WHERE a.event.eventId = :eventId")
    Optional<Chatroom> findByEventId(@Param("eventId") Long eventId);
}
