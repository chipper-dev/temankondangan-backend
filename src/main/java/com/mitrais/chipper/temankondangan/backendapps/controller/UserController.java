package com.mitrais.chipper.temankondangan.backendapps.controller;

import java.text.ParseException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.User;
import com.mitrais.chipper.temankondangan.backendapps.model.json.UserChangePasswordWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.UserPrincipal;
import com.mitrais.chipper.temankondangan.backendapps.security.oauth2.CurrentUser;
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
	@PostMapping("/change-password")
	public ResponseEntity<ResponseBody> changePassword(@RequestBody UserChangePasswordWrapper wrapper, Locale locale,
			HttpServletRequest request) throws ParseException {

		boolean result = userService.changePassword(wrapper);
		if (result) {
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));
		}
		return new ResponseEntity<>(
				getResponseBody(HttpStatus.UNPROCESSABLE_ENTITY, null, null, request.getRequestURI()),
				HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
