package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageListWrapper;

import java.util.List;

public interface ChatService {

	void saveChat(ChatMessage chatMessage, Long roomId);

	ChatMessageListWrapper getChatListByChatroomIdAndUserId(String header, Long chatroomId, Long userId, int pageNumber,
			int pageSize);

	void markChatsAsReceived(String header, List<Long> chatIds, Long userId);

	void markChatAsReceived(String header, Long chatId, Long userId);

	void markChatAsReceivedByChatroomIdAndLastChatId(String header, Long chatroomId, Long lastChatId, Long userId);

	void markChatsAsRead(String header, List<Long> chatIds, Long userId);

	void markChatAsRead(String header, Long chatId, Long userId);

	void markChatAsReadByChatroomIdAndLastChatId(String header, Long chatroomId, Long lastChatId, Long userId);
}
