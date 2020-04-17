package com.mitrais.chipper.temankondangan.backendapps.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mitrais.chipper.temankondangan.backendapps.common.CommonResource;
import com.mitrais.chipper.temankondangan.backendapps.common.response.ResponseBody;
import com.mitrais.chipper.temankondangan.backendapps.model.Gender;
import com.mitrais.chipper.temankondangan.backendapps.model.Profile;
import com.mitrais.chipper.temankondangan.backendapps.model.json.ProfileUpdateWrapper;
import com.mitrais.chipper.temankondangan.backendapps.security.TokenProvider;
import com.mitrais.chipper.temankondangan.backendapps.service.ProfileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Profile Management System", description = "Operations regarding profile in TemenKondangan System")
@RestController
@Validated
@RequestMapping("/profile")
public class ProfileController extends CommonResource {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private TokenProvider tokenProvider;

	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

	@ApiOperation(value = "Update Optional Profile", response = ResponseEntity.class)
	@PostMapping("/update")
	public ResponseEntity<ResponseBody> update(@RequestParam("file") MultipartFile file,
			@RequestParam("token") String token, @RequestParam("fullName") String fullName,
			@RequestParam("dob") String dob, @RequestParam("gender") Gender gender, @RequestParam("city") String city,
			@RequestParam("aboutMe") String aboutMe, @RequestParam("interest") String interest,
			HttpServletRequest request) throws ParseException {
		
		boolean result = profileService.update(new ProfileUpdateWrapper(file, tokenProvider.getUserIdFromToken(token),
				fullName, formatter.parse(dob), gender, city, aboutMe, interest));
		if (result) {
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), result, null));
		}
		return new ResponseEntity<>(getResponseBody(HttpStatus.NOT_MODIFIED, null, null, request.getRequestURI()),
				HttpStatus.NOT_MODIFIED);
	}

	@ApiOperation(value = "Get Profile From Token", response = ResponseEntity.class)
	@GetMapping("/find")
	public ResponseEntity<ResponseBody> findByUserId(@RequestParam("token") String token, HttpServletRequest request) {
		try {
			Long userId = tokenProvider.getUserIdFromToken(token);
			Profile profile = profileService.findByUserId(userId)
					.orElseThrow(() -> new NoSuchElementException("No profile with user id : " + userId));
			return ResponseEntity.ok(getResponseBody(HttpStatus.OK.value(), profile, request.getRequestURI()));
		} catch (NoSuchElementException e) {
			return new ResponseEntity<>(
					getResponseBody(HttpStatus.NOT_FOUND, "No profile from token ", null, request.getRequestURI()),
					HttpStatus.NOT_FOUND);
		}
	}
}
