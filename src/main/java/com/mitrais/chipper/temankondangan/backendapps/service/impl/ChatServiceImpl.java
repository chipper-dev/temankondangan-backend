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
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageListWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatroomListResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.*;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatService;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	public ChatMessageListWrapper getChatListByChatroomIdAndUserId(Long chatroomId, Long userId, int pageNumber,
			int pageSize) {
		List<ChatMessageWrapper> chats = chatRepository.findAllByChatroomIdAndUserId(chatroomId, userId);
		return ChatMessageListWrapper.builder().pageNumber(pageNumber).pageSize(pageSize).actualSize(chats.size())
				.contentList(
						chats.stream().skip((pageNumber - 1) * pageSize).limit(pageSize).collect(Collectors.toList()))
				.build();
	}

	@Override
	public void markChatsAsReceived(List<Long> chatIds, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		if (chatIds.isEmpty()) {
			throw new BadRequestException("Error: ChatIds Id cannot be empty!");
		}

		chatIds.forEach(chatId -> {
			chatRepository.markAsReceivedById(chatId, userId, profile.getFullName(), profile.getFullName());
		});
	}

	@Override
	public void markChatAsReceived(Long chatId, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		chatRepository.markAsReceivedById(chatId, userId, profile.getFullName(), profile.getFullName());
	}

	@Override
	public void markChatAsReceivedByChatroomIdAndLastChatId(Long chatroomId, Long lastChatId, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		chatRepository.markAsReceivedToLastId(chatroomId, lastChatId, userId, profile.getFullName(),
				profile.getFullName());
	}

	@Override
	public void markChatsAsRead(List<Long> chatIds, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		if (chatIds.isEmpty()) {
			throw new BadRequestException("Error: ChatIds Id cannot be empty!");
		}

		chatIds.forEach(chatId -> {
			chatRepository.markAsReadById(chatId, userId, profile.getFullName(), profile.getFullName());
		});

	}

	@Override
	public void markChatAsRead(Long chatId, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		chatRepository.markAsReadById(chatId, userId, profile.getFullName(), profile.getFullName());

	}

	@Override
	public void markChatAsReadByChatroomIdAndLastChatId(Long chatroomId, Long lastChatId, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		chatRepository.markAsReadToLastId(chatroomId, lastChatId, userId, profile.getFullName(), profile.getFullName());
	}
}
