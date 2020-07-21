package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;

import java.util.List;

public interface ChatroomService {

	Chatroom createChatroom(Long eventId);

	List<ChatroomDto> getChatroomListByUserIdSortByDate(Long userId, int pageNumber, int pageSize);

	List<ChatroomDto> getChatroomListByUserIdSortByUnreadChat(Long userId, int pageNumber, int pageSize);

	ChatroomDto getChatroomByIdAndUserId(Long chatroomId, Long userId);

	void deleteChatrooms(List<Long> chatroomIds);

	void markChatroomsAsReceived(List<Long> chatroomIds, Long userId);

	void markChatroomAsReceived(Long chatroomId, Long userId);

	void markChatroomsAsRead(List<Long> chatroomIds, Long userId);

	void markChatroomAsRead(Long chatroomId, Long userId);
	
	Integer getUnreadChatroom(Long userId);
}
