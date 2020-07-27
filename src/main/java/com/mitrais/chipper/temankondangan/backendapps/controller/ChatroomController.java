package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatroomListResponseWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateChatroomWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.DeleteChatroomWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ReadChatroomWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value = "Chatroom Management System")
@RestController
@Validated
@RequestMapping("/chatroom")
public class ChatroomController extends CommonResource {
    @Autowired
    ChatroomService chatroomService;

    @Autowired
    TokenProvider tokenProvider;

	private static final String AUTH_STRING = "Authorization";

    @ApiOperation(value = "Create Chatroom", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @PostMapping("/create-room")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> createChatroom(@RequestBody CreateChatroomWrapper wrapper, HttpServletRequest request) {
        Chatroom chatroom = chatroomService.createChatroom(wrapper.getEventId());
        return ResponseEntity.ok(
                getResponseBody(HttpStatus.CREATED.value(), chatroom, request.getRequestURI()));
    }

	@ApiOperation(value = "Get Chatroom By Id", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@GetMapping("/get-chatroom")
	@ApiResponses(value = { @ApiResponse(response = ChatroomDto.class, code = 200, message = ""),
			@ApiResponse(code = 404, message = "Chatroom not found with chatroomId") })
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> getChatroomById(@RequestParam(defaultValue = "0") Long chatroomId,
														HttpServletRequest request) {
		String token = getToken(request.getHeader(AUTH_STRING));
		Long userId = tokenProvider.getUserIdFromToken(token);
		ChatroomDto chatroom = chatroomService.getChatroomByIdAndUserId(chatroomId, userId);

		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), chatroom, request.getRequestURI()));
	}

    @ApiOperation(value = "Delete Chatrooms", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @PostMapping("/delete-room")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> deleteChatrooms(@RequestBody DeleteChatroomWrapper wrapper, HttpServletRequest request) {
        chatroomService.deleteChatrooms(wrapper.getChatroomId());
        return ResponseEntity.ok(
                getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
    }


	@ApiOperation(value = "Get Chatroom List", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@ApiResponses(value = { @ApiResponse(response = ChatroomListResponseWrapper.class, code = 200, message = ""), })
	@GetMapping("/get-chatroom-list")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> getChatroomList(@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@ApiParam(value = "input timeReceived or unreadMessage") @RequestParam(defaultValue = "timeReceived") String sortBy,
			HttpServletRequest request) {
		String token = getToken(request.getHeader(AUTH_STRING));
		Long userId = tokenProvider.getUserIdFromToken(token);
		ChatroomListResponseWrapper chatrooms;

		if ("timeReceived".equalsIgnoreCase(sortBy)) {
			chatrooms = chatroomService.getChatroomListByUserIdSortByDate(userId, pageNumber, pageSize);
		} else if ("unreadMessage".equalsIgnoreCase(sortBy)) {
			chatrooms = chatroomService.getChatroomListByUserIdSortByUnreadChat(userId, pageNumber, pageSize);
		} else {
			chatrooms = chatroomService.getChatroomListByUserIdSortByDate(userId, pageNumber, pageSize);
		}

		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), chatrooms, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark All Chat as Received in Chatroom", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-received-chatroom")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReceivedChatroom(@RequestBody Long chatroomId, HttpServletRequest request) {
		String token = getToken(request.getHeader(AUTH_STRING));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatroomService.markChatroomAsReceived(chatroomId, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark All Chat as Received in Chatrooms", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-received-chatrooms")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReceivedChatrooms(@RequestBody ReadChatroomWrapper wrapper,
			HttpServletRequest request) {
		String token = getToken(request.getHeader(AUTH_STRING));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatroomService.markChatroomsAsReceived(wrapper.getChatroomIds(), userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark All Chat as Read in Chatroom", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-read-chatroom")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReadChatroom(@RequestBody Long chatroomId, HttpServletRequest request) {
		String token = getToken(request.getHeader(AUTH_STRING));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatroomService.markChatroomAsRead(chatroomId, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark All Chat as Read in Chatrooms", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-read-chatrooms")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReadChatrooms(@RequestBody ReadChatroomWrapper wrapper,
			HttpServletRequest request) {
		String token = getToken(request.getHeader(AUTH_STRING));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatroomService.markChatroomsAsRead(wrapper.getChatroomIds(), userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}

	@ApiOperation(value = "Get unread chatroom count", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@GetMapping("/get-unread-chatrooms")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReadChatrooms(HttpServletRequest request) {
		String token = getToken(request.getHeader(AUTH_STRING));
		Long userId = tokenProvider.getUserIdFromToken(token);
		Integer unreadChatroom = chatroomService.getUnreadChatroom(userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), unreadChatroom, request.getRequestURI()));
	}
}
