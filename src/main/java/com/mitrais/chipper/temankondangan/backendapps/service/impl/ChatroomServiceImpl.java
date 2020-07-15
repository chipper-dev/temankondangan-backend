package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.repository.ChatRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ChatroomRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.ChatroomUserRepository;
import com.mitrais.chipper.temankondangan.backendapps.repository.EventRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatroomServiceImpl implements ChatroomService {

    ChatroomRepository chatroomRepository;
    ChatroomUserRepository chatroomUserRepository;
    ChatRepository chatRepository;
    EventRepository eventRepository;

    @Autowired
    public ChatroomServiceImpl(ChatroomRepository chatroomRepository, ChatroomUserRepository chatroomUserRepository,
                               ChatRepository chatRepository, EventRepository eventRepository) {
        this.chatroomRepository = chatroomRepository;
        this.chatroomUserRepository = chatroomUserRepository;
        this.chatRepository = chatRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Chatroom createChatroom(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event", "eventId", eventId));
        Chatroom chatroom = chatroomRepository.findByEventId(eventId).orElse(null);
        if(chatroom == null) {
            chatroom = new Chatroom();
            chatroom.setEvent(event);
            chatroom.setDataState(DataState.ACTIVE);
            chatroom = chatroomRepository.save(chatroom);
        }
        return chatroom;
    }
}
