package com.mitrais.chipper.temankondangan.backendapps.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Chat;
import com.mitrais.chipper.temankondangan.backendapps.model.dto.ChatroomDto;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatService;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

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
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<ResponseBody> getChatByChatroomId(@RequestParam(defaultValue = "0") Long chatroomId,
			HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		List<Chat> chats= chatService.getChatListByChatroomIdAndUserId(chatroomId, userId);

		return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), chats, request.getRequestURI()));
	}
}
