package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;

import java.util.List;

public interface ChatroomService {

    Chatroom createChatroom(Long eventId);

    List<Chatroom> getChatroomList(Long userId);

    void deleteChatrooms(List<Long> chatroomIds);
}
