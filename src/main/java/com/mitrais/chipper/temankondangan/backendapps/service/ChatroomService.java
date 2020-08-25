package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatroomListResponseWrapper;

import java.util.List;

public interface ChatroomService {

	Chatroom createChatroom(Long eventId);

	List<Chatroom> getActiveChatroomListByEventIdAndUserId(Long eventId, Long userId);
	
	ChatroomListResponseWrapper getChatroomListByUserId(Long userId, int pageNumber, int pageSize, String sortBy);

	ChatroomDto getChatroomByIdAndUserId(Long chatroomId, Long userId);

	void deleteChatrooms(List<Long> chatroomIds, Long userId);

	void markChatroomsAsReceived(String header, List<Long> chatroomIds, Long userId);

	void markChatroomAsReceived(String header, Long chatroomId, Long userId);

	void markChatroomsAsRead(String header, List<Long> chatroomIds, Long userId);

	void markChatroomAsRead(String header, Long chatroomId, Long userId);

	Integer getUnreadChatroom(Long userId);
	
	void markAsInactiveAllActiveChatRoomByEventIdAndUserId(Long eventId, Long userId);
}
