package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT a from Chat a WHERE a.chatroom.id = :chatroomId ORDER BY a.id")
    List<Chat> findAllByChatroomId(@Param("chatroomId") Long chatroomId);
}
