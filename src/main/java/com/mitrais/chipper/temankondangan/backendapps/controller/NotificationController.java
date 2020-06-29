package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Notification;
import com.mitrais.chipper.temankondangan.backendapps.model.json.LoginWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.RegisterUserWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.AuthService;
import com.mitrais.chipper.temankondangan.backendapps.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        List<Notification> notification = notificationService.getNotifications(userId);
        return ResponseEntity.ok(
                getResponseBody(HttpStatus.OK.value(), notification, request.getRequestURI()));
    }
}
