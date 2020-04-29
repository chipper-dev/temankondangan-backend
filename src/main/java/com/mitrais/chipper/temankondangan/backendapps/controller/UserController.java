package com.mitrais.chipper.temankondangan.backendapps.controller;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserCreatePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.security.UserPrincipal;
import com.mitrais.chipper.temankondangan.backendapps.security.CurrentUser;
import com.mitrais.chipper.temankondangan.backendapps.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "User Management System", description = "Operations regarding User in TemenKondangan System")
@RestController
@RequestMapping("/user")
public class UserController extends CommonResource {

	@Autowired
	private UserService userService;

	@Autowired
	private TokenProvider tokenProvider;

	@ApiOperation(value = "Change password API", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PutMapping("/change-password")
	public ResponseEntity<ResponseBody> changePassword(@RequestBody UserChangePasswordWrapper wrapper,
			HttpServletRequest request) {
		LOGGER.info("Change user password");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		try {
			boolean result = userService.changePassword(userId, wrapper);
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));

		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
					e.getStatus());
		}
	}

	@ApiOperation(value = "Create password API", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@PutMapping("/create-password")
	public ResponseEntity<ResponseBody> createPassword(@RequestBody UserCreatePasswordWrapper wrapper,
			HttpServletRequest request) {
		LOGGER.info("Create user password");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);

		try {
			boolean result = userService.createPassword(userId, wrapper);
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));

		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
					e.getStatus());
		}
	}

	@ApiOperation(value = "Remove user API", response = ResponseEntity.class)
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "Bearer <access_token>")
	@DeleteMapping("/remove")
	public ResponseEntity<ResponseBody> removeUser(HttpServletRequest request) {
		LOGGER.info("Remove user");
		String token = getToken(request.getHeader("Authorization"));
		Long userId = tokenProvider.getUserIdFromToken(token);
		
		try {
			userService.remove(userId);
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), null, null));

		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
					e.getStatus());
		}
	}

}
