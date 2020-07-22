package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.exception.UnauthorizedException;
import com.mitrais.chipper.temankondangan.backendapps.model.*;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.en.ChatMessage;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatroomListResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.*;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatroomServiceImpl implements ChatroomService {

	public static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

	ChatroomRepository chatroomRepository;
	ChatroomUserRepository chatroomUserRepository;
	ChatRepository chatRepository;
	EventRepository eventRepository;
	ApplicantRepository applicantRepository;
	UserRepository userRepository;
	ProfileRepository profileRepository;

	@Autowired
	public ChatroomServiceImpl(ChatroomRepository chatroomRepository, ChatroomUserRepository chatroomUserRepository,
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
	public ChatroomListResponseWrapper getChatroomListByUserIdSortByDate(Long userId, int pageNumber, int pageSize) {
		List<ChatroomDto> chatrooms = chatroomRepository.findChatroomListByUserIdSortByDate(userId);
		return ChatroomListResponseWrapper
				.builder().pageNumber(pageNumber).pageSize(pageSize).actualSize(chatrooms.size()).contentList(chatrooms
						.stream().skip((pageNumber - 1) * pageSize).limit(pageSize).collect(Collectors.toList()))
				.build();
	}

	@Override
	public ChatroomListResponseWrapper getChatroomListByUserIdSortByUnreadChat(Long userId, int pageNumber,
																			   int pageSize) {
		List<ChatroomDto> chatrooms = chatroomRepository.findChatroomListByUserIdSortByUnreadChat(userId);
		return ChatroomListResponseWrapper
				.builder().pageNumber(pageNumber).pageSize(pageSize).actualSize(chatrooms.size()).contentList(chatrooms
						.stream().skip((pageNumber - 1) * pageSize).limit(pageSize).collect(Collectors.toList()))
				.build();
	}

	@Override
	public ChatroomDto getChatroomByIdAndUserId(Long chatroomId, Long userId) {
		ChatroomDto chatroom = chatroomRepository.findChatroomByIdAndUserId(chatroomId, userId);

		if (chatroom == null) {
			throw new ResourceNotFoundException(Entity.CHATROOM.getLabel(), "id", chatroomId);
		}
		return chatroom;
	}

    @Override
    public void deleteChatrooms(List<Long> chatroomIds) {
        if(chatroomIds.isEmpty()){
            throw new BadRequestException("Error: Chatroom Id cannot be empty!");
        }

        chatroomIds.forEach(chatroomId -> {
            Chatroom room = chatroomRepository.findById(chatroomId).orElse(null);
            if(room != null) {
                room.setDataState(DataState.DELETED);
                chatroomRepository.save(room);
            }
        });
    }

    @Override
    public void saveChat(ChatMessage chatMessage, Long roomId) {
        Chatroom room = chatroomRepository.findById(roomId).get();
        User user = userRepository.findById(chatMessage.getUserId()).get();
        Chat chat = new Chat();
        chat.setChatroom(room);
        chat.setBody(chatMessage.getContent());
        chat.setUser(user);
        chat.setContentType(chatMessage.getContentType());
        chat.setCreatedDate(new Date());
        chatRepository.save(chat);
    }

    @Override
    public List<ChatMessageWrapper> getChat(Long userId, Long roomId) {
        chatroomUserRepository.findByUserIdAndChatroomId(userId, roomId).orElseThrow(
        		() -> new UnauthorizedException("User are not registered in this chatroom!")
		);
        return chatRepository.findAllByChatroomId(roomId);
    }


	@Override
	public void markChatroomsAsReceived(List<Long> chatroomIds, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		if (chatroomIds.isEmpty()) {
			throw new BadRequestException("Error: Chatroom Id cannot be empty!");
		}

		chatroomIds.forEach(chatroomId -> {
			chatroomRepository.markAsReceivedAllChatInChatRoomByChatRoomIdAndUserId(chatroomId, userId,
					profile.getFullName(), profile.getFullName());
		});
	}

	@Override
	public void markChatroomAsReceived(Long chatroomId, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		chatroomRepository.markAsReceivedAllChatInChatRoomByChatRoomIdAndUserId(chatroomId, userId,
				profile.getFullName(), profile.getFullName());
	}

	@Override
	public void markChatroomsAsRead(List<Long> chatroomIds, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		if (chatroomIds.isEmpty()) {
			throw new BadRequestException("Error: Chatroom Id cannot be empty!");
		}

		chatroomIds.forEach(chatroomId -> {
			chatroomRepository.markAsReadAllChatInChatRoomByChatRoomIdAndUserId(chatroomId, userId,
					profile.getFullName(), profile.getFullName());
		});
	}

	@Override
	public void markChatroomAsRead(Long chatroomId, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		chatroomRepository.markAsReadAllChatInChatRoomByChatRoomIdAndUserId(chatroomId, userId, profile.getFullName(),
				profile.getFullName());
	}

	@Override
	public Integer getUnreadChatroom(Long userId) {
		Integer unreadChatroom = chatroomRepository.getUnreadChatroom(userId);
		return unreadChatroom;
	}
}
