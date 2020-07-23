package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;

import java.util.List;

public interface ChatService {

	List<Chat> getChatListByChatroomIdAndUserId(Long chatroomId, Long userId);
	
}
