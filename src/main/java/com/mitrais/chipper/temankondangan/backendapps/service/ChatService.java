package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageListWrapper;

import java.util.List;

public interface ChatService {

	void saveChat(ChatMessage chatMessage, Long roomId);

	ChatMessageListWrapper getChatListByChatroomIdAndUserId(Long chatroomId, Long userId, int pageNumber,
			int pageSize);

	void markChatsAsReceived(List<Long> chatIds, Long userId);

	void markChatAsReceived(Long chatId, Long userId);

	void markChatAsReceivedByChatroomIdAndLastChatId(Long chatroomId, Long lastChatId, Long userId);

	void markChatsAsRead(List<Long> chatIds, Long userId);

	void markChatAsRead(Long chatId, Long userId);

	void markChatAsReadByChatroomIdAndLastChatId(Long chatroomId, Long lastChatId, Long userId);
}
