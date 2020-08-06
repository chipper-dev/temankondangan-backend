package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatroomListResponseWrapper;

import java.util.List;

public interface ChatroomService {

	Chatroom createChatroom(Long eventId);

	ChatroomListResponseWrapper getChatroomListByUserIdSortByDate(Long userId, int pageNumber, int pageSize);

	ChatroomListResponseWrapper getChatroomListByUserIdSortByUnreadChat(Long userId, int pageNumber, int pageSize);

	ChatroomDto getChatroomByIdAndUserId(Long chatroomId, Long userId);

	void deleteChatrooms(List<Long> chatroomIds);

	void markChatroomsAsReceived(String header, List<Long> chatroomIds, Long userId);

	void markChatroomAsReceived(String header, Long chatroomId, Long userId);

	void markChatroomsAsRead(String header, List<Long> chatroomIds, Long userId);

	void markChatroomAsRead(String header, Long chatroomId, Long userId);

	Integer getUnreadChatroom(Long userId);
}
