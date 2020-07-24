package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	@Query("SELECT new com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageWrapper"
			+ "(c.id, c.user, c.contentType, c.createdDate, c.body, "
			+ "case when c.user.userId = :userId then true else false end as isYourMessage, "
			+ "case when  count(distinct cu.id) - case when c.user.userId = :userId then 1 else 2 end > count(distinct cl.id) then false else true end as isReceived, "
			+ "case when  count(distinct cu.id) - case when c.user.userId = :userId then 1 else 2 end > sum(case when cl.readDate is not null then 1 else 0 end) then false else true end as isRead) "
			+ "from Chat c " + "join c.chatroom cr " + "join ChatroomUser cu on cu.chatroom.id = cr.id "
			+ "left join ChatLog cl on c.id = cl.chat.id " + "WHERE c.chatroom.id = :chatroomId " + "GROUP BY c "
			+ "ORDER BY c.createdDate desc, c.id desc")
	List<ChatMessageWrapper> findAllByChatroomIdAndUserId(@Param("chatroomId") Long chatroomId,
			@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query(value = " insert into chat_log (created_by , created_date , last_modified_by, last_modified_date , chat_id , user_id , received_date , read_date ) "
			+ " select :createdBy as created_by, now() as created_date, :lastModifiedBy as last_modified_by, now() as last_modified_date, c.id as chat_id, :userId as user_id, now() as received_date, null as read_date from chat c "
			+ " left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ " where c.id = :chatId and c.user_id <> :userId and cl.id is null" + "", nativeQuery = true)
	void markAsReceivedById(@Param("chatId") Long chatId, @Param("userId") Long userId,
			@Param("createdBy") String createdBy, @Param("lastModifiedBy") String lastModifiedBy);

	@Modifying
	@Transactional
	@Query(value = " insert into chat_log (created_by , created_date , last_modified_by, last_modified_date , chat_id , user_id , received_date , read_date ) "
			+ " select :createdBy as created_by, now() as created_date, :lastModifiedBy as last_modified_by, now() as last_modified_date, c.id as chat_id, :userId as user_id, now() as received_date, null as read_date from chat c "
			+ " left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ " where c.chatroom_id = :chatroomId and c.id <= :lastChatId and c.user_id <> :userId and cl.id is null"
			+ "", nativeQuery = true)
	void markAsReceivedToLastId(@Param("chatroomId") Long chatroomId, @Param("lastChatId") Long lastChatId,
			@Param("userId") Long userId, @Param("createdBy") String createdBy,
			@Param("lastModifiedBy") String lastModifiedBy);

	@Modifying
	@Transactional
	@Query(value = " update chat_log set read_date = now() where chat_id = :chatId and user_id = :userId ; "
			+ " insert into chat_log (created_by , created_date , last_modified_by, last_modified_date , chat_id , user_id , received_date , read_date ) "
			+ " select :createdBy as created_by, now() as created_date, :lastModifiedBy as last_modified_by, now() as last_modified_date, c.id as chat_id, :userId as user_id, now() as received_date, now() as read_date from chat c "
			+ " left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ " where c.id = :chatId and c.user_id <> :userId and cl.id is null" + "", nativeQuery = true)
	void markAsReadById(@Param("chatId") Long chatId, @Param("userId") Long userId,
			@Param("createdBy") String createdBy, @Param("lastModifiedBy") String lastModifiedBy);

	@Modifying
	@Transactional
	@Query(value = " update chat_log cl set read_date = now() from chat c "
			+ " inner join chatroom cr on c.chatroom_id = cr.id "
			+ " where c.id = cl.chat_id and cr.id = :chatroomId and c.user_id <> :userId and cl.user_id = :userId and cl.read_date is null and c.id <= :lastChatId ; "
			+ " insert into chat_log (created_by , created_date , last_modified_by, last_modified_date , chat_id , user_id , received_date , read_date ) "
			+ " select :createdBy as created_by, now() as created_date, :lastModifiedBy as last_modified_by, now() as last_modified_date, c.id as chat_id, :userId as user_id, now() as received_date, now() as read_date from chat c "
			+ " left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ " where c.chatroom_id = :chatroomId and c.id <= :lastChatId and c.user_id <> :userId and cl.id is null"
			+ "", nativeQuery = true)
	void markAsReadToLastId(@Param("chatroomId") Long chatroomId, @Param("lastChatId") Long lastchatId,
			@Param("userId") Long userId, @Param("createdBy") String createdBy,
			@Param("lastModifiedBy") String lastModifiedBy);

}
