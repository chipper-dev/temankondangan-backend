package com.mitrais.chipper.temankondangan.backendapps.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageListWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ChatMessageWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatService;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Chat Management System")
@RestController
@Validated
@RequestMapping("/chat")
public class ChatController extends CommonResource {
	@Autowired
	ChatService chatService;

	@Autowired
	TokenProvider tokenProvider;

	@ApiOperation(value = "Get Chat By ChatroomId", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@GetMapping("/get-chat")
	@ApiResponses(value = { @ApiResponse(response = ChatMessageListWrapper.class, code = 200, message = ""), })
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> getChatByChatroomId(@RequestParam(defaultValue = "0") Long chatroomId,
			@RequestParam(defaultValue = "1") Integer pageNumber, @RequestParam(defaultValue = "20") Integer pageSize,
			HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		ChatMessageListWrapper chats = chatService.getChatListByChatroomIdAndUserId(chatroomId, userId, pageNumber,
				pageSize);

		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), chats, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark Chat as Received", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-received-chat")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReceivedChat(@RequestBody Long chatId, HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatService.markChatAsReceived(chatId, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark Chat as Received By ChatroomId To Last Chat Id", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-received-chat-tolastid")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReceivedChatToLastId(@RequestBody Long chatroomId,
			@RequestBody Long lastChatId, HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatService.markChatAsReceivedByChatroomIdAndLastChatId(chatroomId, lastChatId, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark Chat as Read", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-read-chat")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReadChat(@RequestBody Long chatId, HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatService.markChatAsRead(chatId, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}

	@ApiOperation(value = "Mark Chat as Read By ChatroomId To Last Chat Id", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PostMapping("/set-read-chat-tolastid")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> setReadChatToLastId(@RequestBody Long chatroomId, @RequestBody Long lastChatId,
			HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		chatService.markChatAsReadByChatroomIdAndLastChatId(chatroomId, lastChatId, userId);
		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
	}
}
