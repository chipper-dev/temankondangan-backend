package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.Applicant;
import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.ChatroomUser;
import com.mitrais.chipper.temankondangan.backendapps.model.Event;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.repository.*;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatService;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

	public static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	ChatroomRepository chatroomRepository;
	ChatroomUserRepository chatroomUserRepository;
	ChatRepository chatRepository;
	EventRepository eventRepository;
	ApplicantRepository applicantRepository;
	UserRepository userRepository;
	ProfileRepository profileRepository;

	@Autowired
	public ChatServiceImpl(ChatroomRepository chatroomRepository, ChatroomUserRepository chatroomUserRepository,
			ChatRepository chatRepository, EventRepository eventRepository, ApplicantRepository applicantRepository,
			UserRepository userRepository, ProfileRepository profileRepository) {
		this.chatroomRepository = chatroomRepository;
		this.chatroomUserRepository = chatroomUserRepository;
		this.chatRepository = chatRepository;
		this.eventRepository = eventRepository;
		this.applicantRepository = applicantRepository;
		this.userRepository = userRepository;
		this.profileRepository = profileRepository;
	}

	@Override
	public List<Chat> getChatListByChatroomIdAndUserId(Long chatroomId, Long userId) {
		List<Chat> chats= chatRepository.findByChatroomIdAndUserId(chatroomId);
		return chats;
	}	
}
