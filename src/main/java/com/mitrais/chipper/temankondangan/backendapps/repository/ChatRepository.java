package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageWrapper"
            + "(a.id, a.user, a.contentType, a.createdDate, a.body) "
            + "from Chat a WHERE a.chatroom.id = :chatroomId ORDER BY a.id")
    List<ChatMessageWrapper> findAllByChatroomId(@Param("chatroomId") Long chatroomId);
}
