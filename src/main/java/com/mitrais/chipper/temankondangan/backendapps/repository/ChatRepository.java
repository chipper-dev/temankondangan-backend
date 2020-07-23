package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	@Query("SELECT a from Chat a WHERE a.chatroom.id = :chatroomId")
	List<Chat> findByChatroomIdAndUserId(@Param("chatroomId") Long chatroomId);
}
