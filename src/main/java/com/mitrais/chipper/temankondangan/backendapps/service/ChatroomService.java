package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatroomListResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;

import java.util.List;

public interface ChatroomService {

	Chatroom createChatroom(Long eventId);

	ChatroomListResponseWrapper getChatroomListByUserIdSortByDate(Long userId, int pageNumber, int pageSize);

	ChatroomListResponseWrapper getChatroomListByUserIdSortByUnreadChat(Long userId, int pageNumber, int pageSize);

    List<Chat> getChat(Long roomId);

	ChatroomDto getChatroomByIdAndUserId(Long chatroomId, Long userId);

	void deleteChatrooms(List<Long> chatroomIds);

	void saveChat(ChatMessage chatMessage, Long roomId);

	void markChatroomsAsReceived(List<Long> chatroomIds, Long userId);

	void markChatroomAsReceived(Long chatroomId, Long userId);

	void markChatroomsAsRead(List<Long> chatroomIds, Long userId);

	void markChatroomAsRead(Long chatroomId, Long userId);

	Integer getUnreadChatroom(Long userId);
}
