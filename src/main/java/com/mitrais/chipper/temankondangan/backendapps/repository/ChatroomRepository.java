package com.mitrais.chipper.temankondangan.backendapps.repository;

import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

	@Query("SELECT a from Chatroom a WHERE a.event.eventId = :eventId AND a.dataState = 'ACTIVE'")
	Optional<Chatroom> findActiveChatroomByEventId(@Param("eventId") Long eventId);
	
	@Query("SELECT a from Chatroom a join ChatroomUser cu ON a.id = cu.chatroom.id WHERE a.event.eventId = :eventId AND a.dataState = 'ACTIVE' and cu.user.userId = :userId ")
	Optional<List<Chatroom>> findActiveChatroomByEventIdAndUserId(@Param("eventId") Long eventId,
			@Param("userId") Long userId);

	@Query(value = "select c.id, c.created_by as createdBy , c.created_date as createdDate , c.last_modified_by as lastModifiedBy , c.last_modified_date as lastModifiedDate ,  "
			+ "	case when e.cancelled or e.data_state <> 'ACTIVE' then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end else  "
			+ "		case when e.finish_date_time is not null and now() > e.finish_date_time then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end "
			+ "		else  "
			+ "			case when e.finish_date_time is null and now() > e.start_date_time then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end "
			+ "			else case when c.data_state = 'INACTIVE' then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end else c.data_state end " 
			+ "			end " + "		end " + "	end as dataState ,  "
			+ "	c.event_id as eventId, "
			+ "	count(distinct ch.id) as unreadChat, e.additional_info as eventAdditionalInfo , e.title as eventTitle, e.start_date_time as startDateTime ,  "
			+ "	e.finish_date_time as finishDateTime , e.cancelled , p.full_name eventCreatorName "
			+ " from chatroom c " + "	inner join chatroom_user cu on c.id = cu.chatroom_id "
			+ "	inner join event e on c.event_id = e.event_id " + "	inner join users u on e.user_id = u.user_id  "
			+ "	inner join profile p on u.user_id = p.user_id  "
			+ " left outer join chat chat on c.id = chat.chatroom_id "
			+ "	left outer join  (   "
			+ "		select c.id, c.chatroom_id, c.user_id chat_user_id, cl.user_id chat_log_user_id  "
			+ "		from chat c " + " 	left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ "		where c.user_id <> :userId and cl.read_date is null ) ch on c.id = ch.chatroom_id "
			+ " left outer join chat c2 on c.id = c2.chatroom_id "
			+ " left outer join chat_log cl2 on c2.id = cl2.chat_id " + " where cu.user_id = :userId  "
			+ " and c.data_state <> 'DELETED' "
			+ " group by c.id, c.created_by , c.created_date , c.last_modified_by , c.last_modified_date , c.event_id, c.data_state, "
			+ "	e.additional_info , e.title , e.start_date_time , e.finish_date_time , e.cancelled, p.full_name , e.data_state  "
			+ "order by max(case when c2.created_date is null then 0 else 1 end) desc, max(cl2.created_date) desc, max(c2.created_date) desc, max(c.created_date) desc ", nativeQuery = true)
	List<ChatroomDto> findChatroomListByUserIdSortByDate(@Param("userId") Long userId);

	@Query(value = "select c.id, c.created_by as createdBy , c.created_date as createdDate , c.last_modified_by as lastModifiedBy , c.last_modified_date as lastModifiedDate ,  "
			+ "	case when e.cancelled or e.data_state <> 'ACTIVE' then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end else  "
			+ "		case when e.finish_date_time is not null and now() > e.finish_date_time then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end "
			+ "		else  "
			+ "			case when e.finish_date_time is null and now() > e.start_date_time then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end	 "
			+ "			else case when c.data_state = 'INACTIVE' then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end else c.data_state end " 
			+ "			end " + "		end " + "	end as dataState ,  "
			+ "	c.event_id as eventId, "
			+ "	count(distinct ch.id) as unreadChat, e.additional_info as eventAdditionalInfo , e.title as eventTitle, e.start_date_time as startDateTime ,  "
			+ "	e.finish_date_time as finishDateTime , e.cancelled , p.full_name eventCreatorName "
			+ " from chatroom c " + "	inner join chatroom_user cu on c.id = cu.chatroom_id "
			+ "	inner join event e on c.event_id = e.event_id " + "	inner join users u on e.user_id = u.user_id  "
			+ "	inner join profile p on u.user_id = p.user_id  "
			+ " left outer join chat chat on c.id = chat.chatroom_id "
			+ "	left outer join  (   "
			+ "		select c.id, c.chatroom_id, c.user_id chat_user_id, cl.user_id chat_log_user_id  "
			+ "		from chat c " + " 	left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ "		where c.user_id <> :userId and cl.read_date is null ) ch on c.id = ch.chatroom_id "
			+ " where cu.user_id = :userId  " + " and c.data_state <> 'DELETED' "
			+ " group by c.id, c.created_by , c.created_date , c.last_modified_by , c.last_modified_date , c.event_id, c.data_state, "
			+ "	e.additional_info , e.title , e.start_date_time , e.finish_date_time , e.cancelled, p.full_name , e.data_state  "
			+ " order by count(distinct ch.id) desc ", nativeQuery = true)
	List<ChatroomDto> findChatroomListByUserIdSortByUnreadChat(@Param("userId") Long userId);

	@Query(value = "select c.id, c.created_by as createdBy , c.created_date as createdDate , c.last_modified_by as lastModifiedBy , c.last_modified_date as lastModifiedDate ,  "
			+ "	case when e.cancelled or e.data_state <> 'ACTIVE' then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end else  "
			+ "		case when e.finish_date_time is not null and now() > e.finish_date_time then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end "
			+ "		else  "
			+ "			case when e.finish_date_time is null and now() > e.start_date_time then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end "
			+ "			else case when c.data_state = 'INACTIVE' then case when count(distinct chat.id) > 0 then 'INACTIVE' else 'DELETED' end else c.data_state end " + "			end " + "		end " + "	end as dataState ,  "
			+ "	c.event_id as eventId, "
			+ "	count(distinct ch.id) as unreadChat, e.additional_info as eventAdditionalInfo , e.title as eventTitle, e.start_date_time as startDateTime ,  "
			+ "	e.finish_date_time as finishDateTime , e.cancelled , p.full_name eventCreatorName "
			+ " from chatroom c " + "	inner join chatroom_user cu on c.id = cu.chatroom_id "
			+ "	inner join event e on c.event_id = e.event_id " + "	inner join users u on e.user_id = u.user_id  "
			+ "	inner join profile p on u.user_id = p.user_id  "
			+ " left outer join chat chat on c.id = chat.chatroom_id " 
			+ "	left outer join  ( "
			+ "		select c.id, c.chatroom_id, c.user_id chat_user_id, cl.user_id chat_log_user_id  "
			+ "		from chat c  " + "		left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ "		where c.user_id <> :userId and cl.read_date is null )  "
			+ "	ch on c.id = ch.chatroom_id and ch.chat_log_user_id is null  "
			+ " where cu.user_id = :userId and c.id = :id " + " and c.data_state <> 'DELETED' "
			+ " group by c.id, c.created_by , c.created_date , c.last_modified_by , c.last_modified_date , c.event_id, c.data_state, "
			+ "	e.additional_info , e.title , e.start_date_time , e.finish_date_time , e.cancelled, p.full_name , e.data_state", nativeQuery = true)
	ChatroomDto findChatroomByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

	@Query(value = "select count(id) from (select c.id from chatroom c  "
			+ "	inner join chatroom_user cu on c.id = cu.chatroom_id "
			+ "	inner join event e on c.event_id = e.event_id " + "	left outer join  (   "
			+ "		select c.id, c.chatroom_id, c.user_id chat_user_id, cl.user_id chat_log_user_id  "
			+ "		from chat c  " + " left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ "		where c.user_id <> :userId and cl.read_date is null ) ch on c.id = ch.chatroom_id "
			+ " where cu.user_id = :userId  " + "group by c.id "
			+ "having count(distinct ch.id) > 0) as tbl ", nativeQuery = true)
	public Integer getUnreadChatroom(@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query(value = "insert into chat_log (created_by , created_date , last_modified_by, last_modified_date , chat_id , user_id , received_date , read_date ) "
			+ "select :createdBy as created_by, now() as created_date, :lastModifiedBy as last_modified_by, now() as last_modified_date, c.id as chat_id, :userId as user_id, now() as received_date, null as read_date from chat c "
			+ "left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ "where c.chatroom_id = :chatroomId and c.user_id <> :userId and cl.id is null "
			+ "order by c.id ", nativeQuery = true)
	void markAsReceivedAllChatInChatRoomByChatRoomIdAndUserId(@Param("chatroomId") Long chatroomId,
			@Param("userId") Long userId, @Param("createdBy") String createdBy,
			@Param("lastModifiedBy") String lastModifiedBy);

	@Modifying
	@Transactional
	@Query(value = "update chat_log cl set read_date = now() "
			+ " from chat c where c.id = cl.chat_id and cl.user_id = :userId "
			+ " and c.chatroom_id = :chatroomId and c.user_id <> :userId and cl.read_date is null; "
			+ " insert into chat_log (created_by , created_date , last_modified_by, last_modified_date , chat_id , user_id , received_date , read_date ) "
			+ " select :createdBy as created_by, now() as created_date, :lastModifiedBy as last_modified_by, now() as last_modified_date, c.id as chat_id, :userId as user_id, now() as received_date, now() as read_date from chat c "
			+ " left outer join chat_log cl on c.id = cl.chat_id and cl.user_id = :userId "
			+ " where c.chatroom_id = :chatroomId and c.user_id <> :userId and cl.id is null " + "order by c.id "
			+ "", nativeQuery = true)
	void markAsReadAllChatInChatRoomByChatRoomIdAndUserId(@Param("chatroomId") Long chatroomId,
			@Param("userId") Long userId, @Param("createdBy") String createdBy,
			@Param("lastModifiedBy") String lastModifiedBy);
	
	@Modifying
	@Transactional
	@Query(value = "update chatroom c set data_state = 'INACTIVE' "
			+ "from chatroom_user cu "
			+ "where c.id = cu.chatroom_id and cu.user_id = :userId and c.event_id = :eventId and c.data_state = 'ACTIVE'", nativeQuery = true)
	void markAsInactiveAllActiveChatRoomByEventIdAndUserId(@Param("eventId") Long eventId,
			@Param("userId") Long userId);
}
