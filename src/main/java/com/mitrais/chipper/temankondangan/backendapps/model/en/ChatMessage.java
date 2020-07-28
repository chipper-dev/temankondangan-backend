package com.mitrais.chipper.temankondangan.backendapps.model.en;

public class ChatMessage {
    private MessageType type;
    private ContentType contentType;
    private String content;
    private String sender;
    private Long userId;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public enum ContentType {
        TEXT
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public ContentType getContentType() { return contentType; }

    public void setContentType(ContentType contentType) { this.contentType = contentType; }
}