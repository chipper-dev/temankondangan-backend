package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebsocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatroomService chatroomService;

    @MessageMapping("/sendMessage/{roomId}")
    public void sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable String roomId) {
        chatroomService.saveChat(chatMessage, Long.parseLong(roomId));
        messagingTemplate.convertAndSend("/room/" + roomId, chatMessage);
    }

    @MessageMapping("/join/{roomId}")
    public void joinRoom(@Payload ChatMessage chatMessage, @DestinationVariable String roomId,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        messagingTemplate.convertAndSend("/room/" + roomId, chatMessage);
    }

}