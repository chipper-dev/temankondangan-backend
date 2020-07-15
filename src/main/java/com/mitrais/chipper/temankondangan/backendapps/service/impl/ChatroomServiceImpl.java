package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.ChatroomUser;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.repository.*;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatroomServiceImpl implements ChatroomService {

    ChatroomRepository chatroomRepository;
    ChatroomUserRepository chatroomUserRepository;
    ChatRepository chatRepository;
    EventRepository eventRepository;
    ApplicantRepository applicantRepository;

    @Autowired
    public ChatroomServiceImpl(ChatroomRepository chatroomRepository, ChatroomUserRepository chatroomUserRepository,
                               ChatRepository chatRepository, EventRepository eventRepository,
                               ApplicantRepository applicantRepository) {
        this.chatroomRepository = chatroomRepository;
        this.chatroomUserRepository = chatroomUserRepository;
        this.chatRepository = chatRepository;
        this.eventRepository = eventRepository;
        this.applicantRepository = applicantRepository;
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

            ChatroomUser userCreator = new ChatroomUser();
            userCreator.setChatroom(chatroom);
            userCreator.setUser(event.getUser());
            chatroomUserRepository.save(userCreator);

            List<Applicant> applicantsApproved = applicantRepository.findByEventIdAccepted(eventId);
            Chatroom finalChatroom = chatroom;
            applicantsApproved.forEach(applicant -> {
                ChatroomUser userApplicant = new ChatroomUser();
                userApplicant.setChatroom(finalChatroom);
                userApplicant.setUser(applicant.getApplicantUser());
                chatroomUserRepository.save(userApplicant);
            });
        }
        return chatroom;
    }

    @Override
    public List<Chatroom> getChatroomList(Long userId) {
        List<ChatroomUser> list = chatroomUserRepository.findByUserId(userId);
        List<Chatroom> chatrooms = new ArrayList<>();
        list.forEach(data -> {
            chatrooms.add(data.getChatroom());
        });
        return chatrooms;
    }
}
