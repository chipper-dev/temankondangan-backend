package com.mitrais.chipper.temankondangan.backendapps.controller;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ForgotPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ResetPasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(value = "User Management System")
@RestController
@RequestMapping("/user")
public class UserController extends CommonResource {

    private static final String HEADER_AUTH = "Authorization";

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @ApiOperation(value = "Change password API", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @PutMapping("/change-password")
    public ResponseEntity<ResponseBody> changePassword(@RequestBody UserChangePasswordWrapper wrapper,
                                                       HttpServletRequest request) {

        String token = getToken(request.getHeader(HEADER_AUTH));
        Long userId = tokenProvider.getUserIdFromToken(token);
        boolean result = userService.changePassword(userId, wrapper);
        return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));
    }

    @ApiOperation(value = "Create password API", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @PutMapping("/create-password")
    public ResponseEntity<ResponseBody> createPassword(@RequestBody UserCreatePasswordWrapper wrapper,
                                                       HttpServletRequest request) {
        LOGGER.info("Create user password");
        String token = getToken(request.getHeader(HEADER_AUTH));
        Long userId = tokenProvider.getUserIdFromToken(token);
        boolean result = userService.createPassword(userId, wrapper);
        return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));

    }

    @ApiOperation(value = "Remove user API", response = ResponseEntity.class)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
    @DeleteMapping("/remove")
    public ResponseEntity<ResponseBody> removeUser(HttpServletRequest request) {
        LOGGER.info("Remove user");
        String token = getToken(request.getHeader(HEADER_AUTH));
        Long userId = tokenProvider.getUserIdFromToken(token);
        userService.remove(userId);
        return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, null));

    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> forgotPassword(@RequestBody ForgotPasswordWrapper data, HttpServletRequest request) {
        userService.forgotPassword(data.getEmail());
        return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), "Verification code already sent to your email. Please check your email", request.getRequestURI()));
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseBody> resetPassword(@RequestBody ResetPasswordWrapper wrapper, HttpServletRequest request) {
        userService.resetPassword(wrapper);
        return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), "Your password is updated successfully. Please try to login with your new password", request.getRequestURI()));
    }

}
