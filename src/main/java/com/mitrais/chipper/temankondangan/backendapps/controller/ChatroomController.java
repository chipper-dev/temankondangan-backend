package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Chatroom;
import com.mitrais.chipper.temankondangan.backendapps.model.json.CreateChatroomWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.ChatroomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "Chatroom Management System")
@RestController
@Validated
@RequestMapping("/chatroom")
public class ChatroomController extends CommonResource {
    @Autowired
    ChatroomService chatroomService;

    @Autowired
    TokenProvider tokenProvider;

    @ApiOperation(value = "Create Chatroom", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @PostMapping("/create-room")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> createChatroom(@RequestBody CreateChatroomWrapper wrapper, HttpServletRequest request) {
        Chatroom chatroom = chatroomService.createChatroom(wrapper.getEventId());
        return ResponseEntity.ok(
                getResponseBody(HttpStatus.OK.value(), chatroom, request.getRequestURI()));
    }

    @ApiOperation(value = "Get Chatroom List", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @GetMapping("/get-chatroom-list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> getNotification(HttpServletRequest request) {
        String token = getToken(request.getHeader("Authorization"));
        Long userId = tokenProvider.getUserIdFromToken(token);
        List<Chatroom> chatrooms = chatroomService.getChatroomList(userId);
        return ResponseEntity.ok(
                getResponseBody(HttpStatus.OK.value(), chatrooms, request.getRequestURI()));
    }
}
