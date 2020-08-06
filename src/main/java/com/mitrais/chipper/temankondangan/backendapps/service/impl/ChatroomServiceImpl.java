package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonFunction;
import com.mitrais.chipper.temankondangan.backendapps.exception.BadRequestException;
import com.mitrais.chipper.temankondangan.backendapps.exception.ResourceNotFoundException;
import com.mitrais.chipper.temankondangan.backendapps.model.*;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.en.DataState;
import com.mitrais.chipper.temankondangan.backendapps.model.en.Entity;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatroomListResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.repository.*;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatroomServiceImpl implements ChatroomService {

	public static final Logger LOGGER = LoggerFactory.getLogger(ChatroomServiceImpl.class);

	public static final String ERROR_CHATROOM_ID_EMPTY = "Error: Chatroom Id cannot be empty!";

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

        if(Boolean.TRUE.equals(event.getCancelled())) {
			throw new BadRequestException("Error: This event has been cancelled!");
		} else if(CommonFunction.isEventFinished(event.getStartDateTime(), event.getFinishDateTime())) {
			throw new BadRequestException("Error: This event has finished already");
		}

        Chatroom chatroom = chatroomRepository.findByEventId(eventId).orElse(null);
		List<Applicant> applicantsApproved = applicantRepository.findByEventIdAccepted(eventId);
		if(chatroom == null) {
        	createAndSaveChatroom(event, applicantsApproved);

        } else {
        	if(DataState.INACTIVE.equals(chatroom.getDataState())) {
				createAndSaveChatroom(event, applicantsApproved);
			} else {
				throw new BadRequestException("Error: This room already created by "+ chatroom.getCreatedBy() +"!");
			}
		}
        return chatroom;
    }


	@Override
	public ChatroomListResponseWrapper getChatroomListByUserIdSortByDate(Long userId, int pageNumber, int pageSize) {
		List<ChatroomDto> chatrooms = chatroomRepository.findChatroomListByUserIdSortByDate(userId);
		setChatroomInactive(chatrooms);
		return ChatroomListResponseWrapper
				.builder().pageNumber(pageNumber).pageSize(pageSize).actualSize(chatrooms.size()).contentList(chatrooms
						.stream().skip((long)(pageNumber - 1) * pageSize).limit(pageSize).collect(Collectors.toList()))
				.build();
	}

	@Override
	public ChatroomListResponseWrapper getChatroomListByUserIdSortByUnreadChat(Long userId, int pageNumber,
																			   int pageSize) {
		List<ChatroomDto> chatrooms = chatroomRepository.findChatroomListByUserIdSortByUnreadChat(userId);
		setChatroomInactive(chatrooms);
		return ChatroomListResponseWrapper
				.builder().pageNumber(pageNumber).pageSize(pageSize).actualSize(chatrooms.size()).contentList(chatrooms
						.stream().skip((long)(pageNumber - 1) * pageSize).limit(pageSize).collect(Collectors.toList()))
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
    public void deleteChatrooms(List<Long> chatroomIds, Long userId) {
        if(chatroomIds.isEmpty()){
            throw new BadRequestException(ERROR_CHATROOM_ID_EMPTY);
        }

        chatroomIds.forEach(chatroomId -> {
            Chatroom room = chatroomRepository.findById(chatroomId).orElse(null);
            if(room != null) {
            	ChatroomUser user = chatroomUserRepository.findByUserIdAndChatroomId(userId, chatroomId).orElse(null);

            	if(user == null) {
					throw new BadRequestException("Error: User cannot delete chatroom "+ chatroomId+"!");
				}

                room.setDataState(DataState.DELETED);
                chatroomRepository.save(room);
            } else {
				throw new BadRequestException("Error: Chatroom not exist!");
			}
        });
    }

	@Override
	public void markChatroomsAsReceived(List<Long> chatroomIds, Long userId) {
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException(Entity.USER.getLabel(), "id", userId));

		if (chatroomIds.isEmpty()) {
			throw new BadRequestException(ERROR_CHATROOM_ID_EMPTY);
		}

		chatroomIds.forEach(chatroomId ->
			chatroomRepository.markAsReceivedAllChatInChatRoomByChatRoomIdAndUserId(chatroomId, userId,
					profile.getFullName(), profile.getFullName()));
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
			throw new BadRequestException(ERROR_CHATROOM_ID_EMPTY);
		}

		chatroomIds.forEach(chatroomId ->
			chatroomRepository.markAsReadAllChatInChatRoomByChatRoomIdAndUserId(chatroomId, userId,
					profile.getFullName(), profile.getFullName()));
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
		return chatroomRepository.getUnreadChatroom(userId);
	}

	private void createAndSaveChatroom(Event event, List<Applicant> applicantsApproved) {
		if(applicantsApproved.isEmpty()) {
			throw new BadRequestException("Error: Event did not have approved applicant!");
		}

		Chatroom chatroom = new Chatroom();
		chatroom.setEvent(event);
		chatroom.setDataState(DataState.ACTIVE);
		chatroom = chatroomRepository.save(chatroom);

		ChatroomUser userCreator = new ChatroomUser();
		userCreator.setChatroom(chatroom);
		userCreator.setUser(event.getUser());
		chatroomUserRepository.save(userCreator);

		Chatroom finalChatroom = chatroom;
		applicantsApproved.forEach(applicant -> {
			ChatroomUser userApplicant = new ChatroomUser();
			userApplicant.setChatroom(finalChatroom);
			userApplicant.setUser(applicant.getApplicantUser());
			chatroomUserRepository.save(userApplicant);
		});
	}

	private void setChatroomInactive(List<ChatroomDto> chatrooms){
		chatrooms.forEach(chatroom -> {
			if("INACTIVE".equalsIgnoreCase(chatroom.getDataState())){
				Chatroom chatroomData = chatroomRepository.getOne(chatroom.getId());
				chatroomData.setDataState(DataState.INACTIVE);
				chatroomRepository.save(chatroomData);
			}
		});
	}
}
