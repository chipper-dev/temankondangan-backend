package com.mitrais.chipper.temankondangan.backendapps.model.json;

import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Builder
public class ChatMessageWrapper {
	private Long id;
	private User user;
	private ChatMessage.ContentType contentType;
	private Date createdDate;
	private String body;
	private Boolean isYourMessage;
	private Boolean isReceived;
	private Boolean isRead;

	public ChatMessageWrapper(Long id, User user, ChatMessage.ContentType contentType, Date createdDate, String body,
			Boolean isYourMessage, Boolean isReceived, Boolean isRead) {
		this.id = id;
		this.user = user;
		this.contentType = contentType;
		this.createdDate = createdDate;
		this.body = body;
		this.isYourMessage = isYourMessage;
		this.isReceived = isReceived;
		this.isRead = isRead;
	}
}
