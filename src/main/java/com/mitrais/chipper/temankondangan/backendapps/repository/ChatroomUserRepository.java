package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.ChatroomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatroomUserRepository extends JpaRepository<ChatroomUser, Long> {

    @Query("SELECT a from ChatroomUser a WHERE a.user.userId = :userId")
    List<ChatroomUser> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a from ChatroomUser a WHERE a.user.userId = :userId AND a.chatroom.id = :chatroomId")
    Optional<ChatroomUser> findByUserIdAndChatroomId(@Param("userId") Long userId, @Param("chatroomId") Long chatroomId);
}
