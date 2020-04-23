package com.mitrais.chipper.temankondangan.backendapps.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

	@GetMapping("/me")
	@PreAuthorize("hasRole('USER')")
	public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
		return userService.findById(userPrincipal.getId());

	}

	@ApiOperation(value = "Change password API", response = ResponseEntity.class)
	@PutMapping("/change-password")
	public ResponseEntity<ResponseBody> changePassword(@RequestBody UserChangePasswordWrapper wrapper,
			HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));

		try {
			boolean result = userService.changePassword(wrapper, token);
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));

		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
					e.getStatus());
		}
	}

	@ApiOperation(value = "Create password API", response = ResponseEntity.class)
	@PutMapping("/create-password")
	public ResponseEntity<ResponseBody> createPassword(@RequestBody UserCreatePasswordWrapper wrapper,
			HttpServletRequest request) {
		String token = getToken(request.getHeader("Authorization"));

		try {
			boolean result = userService.createPassword(wrapper, token);
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));

		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(getResponseBody(e.getStatus(), null, e.getReason(), request.getRequestURI()),
					e.getStatus());
		}
	}
}
