package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.json.NotificationDataWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ReadNotificationWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value = "Notification")
@RestController
@RequestMapping("/notification")
public class NotificationController extends CommonResource {

    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenProvider tokenProvider;

    @ApiOperation(value = "Get Notification", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @GetMapping("/get-notification")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> getNotification(HttpServletRequest request) {
        String token = getToken(request.getHeader("Authorization"));
        Long userId = tokenProvider.getUserIdFromToken(token);
        NotificationDataWrapper notificationData = notificationService.getNotifications(userId);
        return ResponseEntity.ok(
                getResponseBody(HttpStatus.OK.value(), notificationData, request.getRequestURI()));
    }

    @ApiOperation(value = "Set Read Notification", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @PostMapping("/set-read-notification")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> setReadNotification(@RequestBody ReadNotificationWrapper wrapper, HttpServletRequest request) {
        String token = getToken(request.getHeader("Authorization"));
        Long userId = tokenProvider.getUserIdFromToken(token);
        notificationService.setReadNotification(wrapper.getNotificationIds(), userId);
        return ResponseEntity.ok(
                getResponseBody(HttpStatus.OK.value(), null, request.getRequestURI()));
    }
}
